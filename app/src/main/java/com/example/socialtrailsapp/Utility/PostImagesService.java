package com.example.socialtrailsapp.Utility;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IPostImagesInterface;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostImages;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PostImagesService implements IPostImagesInterface {

    private DatabaseReference reference;
    private static String _collectionName = "postimages";
    private StorageReference storageReference;

    public PostImagesService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
    @Override
    public void addFoodPhotos(PostImages model, OperationCallback callback) {
        String newItemKey = reference.child(_collectionName).push().getKey();
        model.setImageId(newItemKey);
        reference.child(_collectionName).child(newItemKey).setValue(model)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (callback != null) {
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
    @Override
    public void uploadImages(String postId, ArrayList<Uri> imageUris, OperationCallback callback) {
        for (int i = 0; i < imageUris.size(); i++) {
            int order = i + 1;
            Uri imageUri = imageUris.get(i);
            StorageReference fileReference = storageReference.child("postimages/" + postId + "/" + UUID.randomUUID().toString());

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(downloadUrl -> {

                PostImages photos = new PostImages(postId,downloadUrl.toString(), order);
                addFoodPhotos(photos, new OperationCallback() {
                    @Override
                    public void onSuccess() {
                        // Handle success scenario, possibly update food record with locationId
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        // Handle failure scenario
                        if (callback != null) {
                            callback.onFailure(error);
                        }
                    }
                });
            })).addOnFailureListener(e -> {

            });
        }
    }
    @Override
    public void getAllPhotosByPostId(String uid, DataOperationCallback<List<Uri>> callback) {
        reference.child(_collectionName).orderByChild("postId")
                .equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<PostImages> photosList = new ArrayList<>();
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                PostImages photo = childSnapshot.getValue(PostImages.class);
                                if (photo != null) {
                                    photosList.add(photo);
                                }
                            }

                            photosList.sort(Comparator.comparingInt(PostImages::getOrder));

                            List<Uri> imageUrls = new ArrayList<>();
                            for (PostImages photo : photosList) {
                                Uri uri = Uri.parse(photo.getImagePath());
                                imageUrls.add(uri);
                            }

                            if (callback != null) {
                                callback.onSuccess(imageUrls);

                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure("Photos not found");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError e) {
                        if (callback != null) {
                            callback.onFailure(e.getMessage());
                        }
                    }
                });
    }
}
