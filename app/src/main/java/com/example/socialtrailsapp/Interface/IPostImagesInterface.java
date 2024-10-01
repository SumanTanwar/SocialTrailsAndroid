package com.example.socialtrailsapp.Interface;

import android.net.Uri;

import com.example.socialtrailsapp.ModelData.PostImages;

import java.util.ArrayList;

public interface IPostImagesInterface {
    void addFoodPhotos(PostImages model, OperationCallback callback);
    void uploadImages(String postId, ArrayList<Uri> imageUris, OperationCallback callback);
}
