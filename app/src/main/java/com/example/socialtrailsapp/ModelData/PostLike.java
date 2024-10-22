package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

public class PostLike {
    private String postlikeId;
    private String postId;
    private String userId;
    private String removedby;
    private String removedrole;
    private String createdon;

    public PostLike() {
    }

    public PostLike(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
        this.createdon = Utils.getCurrentDatetime();
    }

    public String getPostlikeId() {
        return postlikeId;
    }

    public void setPostlikeId(String postlikeId) {
        this.postlikeId = postlikeId;
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

    public String getRemovedby() {
        return removedby;
    }

    public void setRemovedby(String removedby) {
        this.removedby = removedby;
    }

    public String getRemovedrole() {
        return removedrole;
    }

    public void setRemovedrole(String removedrole) {
        this.removedrole = removedrole;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }
}
