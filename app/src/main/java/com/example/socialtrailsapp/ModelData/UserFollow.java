package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

public class UserFollow {
    private String followId;
    private String userId;
    private String followersId;
    private String followingId;
    private String removedFollow;
    private String createdOn;

    public UserFollow() {
    }

    public UserFollow(String userId, String followersId, String followingId, String removedFollow) {
        this.userId = userId;
        this.followersId = followersId;
        this.followingId = followingId;
        this.removedFollow = removedFollow;
        this.createdOn = Utils.getCurrentDatetime();
    }

    public String getFollowId() {
        return followId;
    }

    public void setFollowId(String followId) {
        this.followId = followId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFollowersId() {
        return followersId;
    }

    public void setFollowersId(String followersId) {
        this.followersId = followersId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public String getRemovedFollow() {
        return removedFollow;
    }

    public void setRemovedFollow(String removedFollow) {
        this.removedFollow = removedFollow;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
