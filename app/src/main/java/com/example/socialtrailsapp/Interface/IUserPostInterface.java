package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.UserPost;

import java.util.List;

public interface IUserPostInterface {
    void createPost(UserPost userPost, OperationCallback callback);
    void getAllUserPost(String userId, final DataOperationCallback<List<UserPost>> callback);
    void deleteUserPost(String postId,OperationCallback callback);
    void getPostByPostId(String postId, final DataOperationCallback<UserPost> callback);
    void updateUserPost(UserPost post, OperationCallback callback);
    void updateLikeCount(String postId, int change, DataOperationCallback<Integer> callback);
    void getAllUserPostDetail(String userId, final DataOperationCallback<List<UserPost>> callback);
    void retrievePostsForFollowedUsers(String currentUserId, final DataOperationCallback<List<UserPost>> callback);
}
