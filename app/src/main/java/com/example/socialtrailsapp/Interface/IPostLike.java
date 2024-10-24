package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.LikeResult;
import com.example.socialtrailsapp.ModelData.PostLike;
import com.example.socialtrailsapp.ModelData.Users;

import java.util.List;

public interface IPostLike {
    void likeandUnlikePost(String postId,String userId, DataOperationCallback<LikeResult> callback);
    void getPostLikeByUserandPostId(String postId, String userId, DataOperationCallback<PostLike> callback);
    void getLikesForPost(String postId, DataOperationCallback<List<PostLike>> callback);
    void removeLike(String postlikeId,String postId, OperationCallback callback);
    
}
