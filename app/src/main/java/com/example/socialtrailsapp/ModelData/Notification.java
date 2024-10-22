package com.example.socialtrailsapp.ModelData;

public class Notification {
    private String followerName;
    private String followerProfilePic;
    private boolean isRead;

    public Notification() {
    }

    public Notification(String followerName, String followerProfilePic, boolean isRead) {
        this.followerName = followerName;
        this.followerProfilePic = followerProfilePic;
        this.isRead = isRead;
    }

    // Getters
    public String getFollowerName() {
        return followerName;
    }

    public String getFollowerProfilePic() {
        return followerProfilePic; // Ensure this matches your database structure
    }

    public boolean isRead() {
        return isRead;
    }

    // Add a method to return a formatted message
    public String getMessage() {
        return followerName + " has started following you.";
    }
}
