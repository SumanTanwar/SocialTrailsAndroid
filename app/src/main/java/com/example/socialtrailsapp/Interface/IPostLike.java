package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.LikeResult;
import com.example.socialtrailsapp.ModelData.PostLike;

public interface IPostLike {
    void likeandUnlikePost(String postId,String userId, DataOperationCallback<LikeResult> callback);
    void getPostLikeByUserandPostId(String postId, String userId, DataOperationCallback<PostLike> callback);
}
