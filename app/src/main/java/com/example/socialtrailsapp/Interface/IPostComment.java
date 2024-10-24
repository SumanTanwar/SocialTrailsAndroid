package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.PostComment;

import java.util.List;

public interface IPostComment {
    void addPostComment(PostComment data, OperationCallback callback);
    void removePostComment(String commentId, OperationCallback callback);
    void  retrieveComments(String postId, DataOperationCallback<List<PostComment>> callback);
    void deleteAllCommentsForPost(String postId, OperationCallback callback);
}
