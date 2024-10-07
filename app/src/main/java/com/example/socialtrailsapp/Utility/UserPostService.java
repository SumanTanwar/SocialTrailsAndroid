package com.example.socialtrailsapp.Utility;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IUserPostInterface;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void getAllUserPost(String userId, final DataOperationCallback<List<UserPost>> callback) {
        reference.child(_collectionName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserPost> postList = new ArrayList<>();
                List<UserPost> tempList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("FirebaseData", snapshot.toString());
                    UserPost post = snapshot.getValue(UserPost.class);
                    if (post != null && userId.equals(post.getUserId())) {
                        post.setPostId(snapshot.getKey());
                        tempList.add(post);
                    }
                }

                if (tempList.isEmpty()) {
                    callback.onSuccess(postList);
                    return;
                }

                AtomicInteger pendingRequests = new AtomicInteger(tempList.size());
                for (UserPost post : tempList) {
                    getAllPhotos(post.getPostId(), new DataOperationCallback<List<Uri>>() {
                        @Override
                        public void onSuccess(List<Uri> imageUris) {
                            post.setUploadedImageUris(imageUris);
                            postList.add(post);
                            if (pendingRequests.decrementAndGet() == 0) {
                                callback.onSuccess(postList);
                            }
                        }

                        @Override
                        public void onFailure(String error) {

                            if (pendingRequests.decrementAndGet() == 0) {
                                callback.onSuccess(postList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }
    private void getAllPhotos(String donationId, DataOperationCallback<List<Uri>> callback)
    {
        postImagesService.getAllPhotosByPostId(donationId, new DataOperationCallback<List<Uri>>() {
            @Override
            public void onSuccess(List<Uri> imageUri) {

                if (callback != null) {
                    callback.onSuccess(imageUri);
                }
            }

            @Override
            public void onFailure(String error) {
                if (callback != null) {
                    callback.onFailure(error);
                }
            }
        });


    }
}
