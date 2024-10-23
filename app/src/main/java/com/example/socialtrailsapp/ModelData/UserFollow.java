package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UserFollow {
    private String followId;
    private String userId;
    private List<String> followerIds = new ArrayList<>();
    private Map<String, Boolean> followingIds = new HashMap<>();
    private String createdOn;

    public UserFollow() {
        followingIds = new HashMap<>();
        followerIds = new ArrayList<>();
    }

    public UserFollow(String userId) {
        this.userId = userId;
        this.createdOn = Utils.getCurrentDatetime();
        this.followingIds = new HashMap<>();
        this.followerIds = new ArrayList<>();
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

    public List<String> getFollowerIds() {
        return followerIds;
    }

    public void setFollowerIds(List<String> followerIds) {
        this.followerIds = followerIds;
    }

    public Map<String, Boolean> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(Map<String, Boolean> followingIds) {
        this.followingIds = followingIds;
    }

    public void addFollowingId(String followingId, boolean isPendingRequest) {
        followingIds.put(followingId, isPendingRequest);
    }

    public void addFollowerId(String followerId) {
        if (!followerIds.contains(followerId)) {
            followerIds.add(followerId);
        }
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
