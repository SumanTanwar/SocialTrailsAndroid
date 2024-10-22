package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

public class PostComment {
    private String postcommentId;
    private String postId;
    private String userId;
    private String commenttext;
    private String createdon;
    private String username;
    private String userprofilepicture;

    public PostComment()
    {

    }

    public PostComment(String postId, String userId, String commenttext) {
        this.postId = postId;
        this.userId = userId;
        this.commenttext = commenttext;
        this.createdon = Utils.getCurrentDatetime();
    }

    public String getPostcommentId() {
        return postcommentId;
    }

    public void setPostcommentId(String postcommentId) {
        this.postcommentId = postcommentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommenttext() {
        return commenttext;
    }

    public void setCommenttext(String commenttext) {
        this.commenttext = commenttext;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofilepicture() {
        return userprofilepicture;
    }

    public void setUserprofilepicture(String userprofilepicture) {
        this.userprofilepicture = userprofilepicture;
    }
}
