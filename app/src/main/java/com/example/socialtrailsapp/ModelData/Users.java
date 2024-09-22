package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

public class Users {
    private String userId,username,email,createdon,profilepicture,roles;
    private Boolean profiledeleted,notification,admindeleted,suspended,isactive;

    public Users()
    {

    }
    public Users(String userId, String username, String email, String roles) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.createdon = Utils.getCurrentDatetime();
        this.roles = roles;
        this.profiledeleted = false;
        this.notification = true;
        this.admindeleted = false;
        this.suspended = false;
        this.isactive = true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public Boolean getProfiledeleted() {
        return profiledeleted;
    }

    public void setProfiledeleted(Boolean profiledeleted) {
        this.profiledeleted = profiledeleted;
    }

    public Boolean getAdmindeleted() {
        return admindeleted;
    }

    public void setAdmindeleted(Boolean admindeleted) {
        this.admindeleted = admindeleted;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }
}
