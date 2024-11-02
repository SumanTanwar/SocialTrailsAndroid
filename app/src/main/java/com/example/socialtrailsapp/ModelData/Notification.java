package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String notificationId;
    private String notifyto;
    private String type;
    private String message;
    private String relatedId;
    private String createdon;
    private String notifyBy;

    private String username;
    private String userprofilepicture;

    public Notification() {
    }

    public Notification(String notifyto,String notifyBy, String type, String message, String relatedId) {
        this.notifyto = notifyto;
        this.notifyBy = notifyBy;
        this.type = type;
        this.message = message;
        this.relatedId = relatedId;
        this.createdon = Utils.getCurrentDatetime();

    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotifyto() {
        return notifyto;
    }

    public void setNotifyto(String notifyto) {
        this.notifyto = notifyto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
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

    public String getNotifyBy() {
        return notifyBy;
    }

    public void setNotifyBy(String notifyBy) {
        this.notifyBy = notifyBy;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("notificationId", notificationId);
        map.put("notifyto", notifyto);
        map.put("type", type);
        map.put("message", message);
        map.put("relatedId", relatedId);
        map.put("notifyBy", notifyBy);
        map.put("createdon", createdon);
        return map;
    }

}
