package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

public class PostLike {
    private String postlikeId;
    private String postId;
    private String userId;
    private String createdon;
    private String username;
    private String profilepicture;

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

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }
}
