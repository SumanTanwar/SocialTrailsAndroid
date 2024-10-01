package com.example.socialtrailsapp.Utility;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.IUserPostInterface;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserPostService implements IUserPostInterface {

    private DatabaseReference reference;
    private static String _collectionName = "post";
    private PostImagesService postImagesService;

    public UserPostService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        postImagesService = new PostImagesService();
    }
    @Override
    public void createPost(UserPost userPost, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        userPost.setPostId(newItemKey);
        reference.child(_collectionName).child(newItemKey).setValue(userPost.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (callback != null) {
                            postImagesService.uploadImages(newItemKey,userPost.getImageUris(),callback);
                            callback.onSuccess();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
    }
}
