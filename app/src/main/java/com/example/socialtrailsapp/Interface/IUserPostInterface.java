package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.UserPost;

public interface IUserPostInterface {
    void createPost(UserPost userPost, OperationCallback callback);
}
