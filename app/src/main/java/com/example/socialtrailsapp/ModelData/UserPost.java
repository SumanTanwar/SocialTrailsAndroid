package com.example.socialtrailsapp.ModelData;

import android.net.Uri;

import com.example.socialtrailsapp.Utility.PostImagesService;
import com.example.socialtrailsapp.Utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPost {
    private String postId,userId,captiontext,createdon,updatedon,location;
    private Boolean flagged,moderationstatus;
    private ArrayList<Uri> imageUris;
    private Double  latitude,longitude;
    public List<Uri> uploadedImageUris;
    private int likecount;
    private boolean isliked;
    private int commentcount;
    private  String username;
    private String userprofilepicture;
    public UserPost() {
    }

    public UserPost(String userId, String captiontext, String location, Double latitude, Double longitude, ArrayList<Uri> imageUris) {
        this.userId = userId;
        this.captiontext = captiontext;
        this.location = location;
        this.imageUris = imageUris;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdon = Utils.getCurrentDatetime();

    }

    public UserPost(String postId, String captiontext, String location, Double latitude, Double longitude) {
        this.postId = postId;
        this.captiontext = captiontext;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getCaptiontext() {
        return captiontext;
    }

    public void setCaptiontext(String captiontext) {
        this.captiontext = captiontext;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(String updatedon) {
        this.updatedon = updatedon;
    }



    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    public Boolean getModerationstatus() {
        return moderationstatus;
    }

    public void setModerationstatus(Boolean moderationstatus) {
        this.moderationstatus = moderationstatus;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(ArrayList<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    public List<Uri> getUploadedImageUris() {
        return uploadedImageUris;
    }

    public void setUploadedImageUris(List<Uri> uploadedImageUris) {
        this.uploadedImageUris = uploadedImageUris;
    }



    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public boolean isLiked() {
        return isliked;
    }

    public void setLiked(boolean liked) {
        this.isliked = liked;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
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

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("userId", userId);
        result.put("captiontext", captiontext);
        result.put("location", location);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("createdon", createdon);

        return result;
    }
    public Map<String, Object> toMapUpdate() {
        Map<String, Object> result = new HashMap<>();
        result.put("captiontext", captiontext);
        result.put("location", location);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("updatedon", updatedon);

        return result;
    }

}
