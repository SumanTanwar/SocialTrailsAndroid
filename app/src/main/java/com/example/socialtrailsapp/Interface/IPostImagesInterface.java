package com.example.socialtrailsapp.Interface;

import android.net.Uri;

import com.example.socialtrailsapp.ModelData.PostImages;

import java.util.ArrayList;
import java.util.List;

public interface IPostImagesInterface {
    void addFoodPhotos(PostImages model, OperationCallback callback);
    void uploadImages(String postId, ArrayList<Uri> imageUris, OperationCallback callback);
    void getAllPhotosByPostId(String uid, DataOperationCallback<List<Uri>> callback);
    void deleteImage(String postId, String photoPath, OperationCallback callback);
    void updatePhotoOrder(String postId, OperationCallback callback);
    void deleteAllPostImages(String postId, OperationCallback callback);
}
