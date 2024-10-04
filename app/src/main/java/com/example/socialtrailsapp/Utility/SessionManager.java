package com.example.socialtrailsapp.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences sessionsharedPreferences;
    private SharedPreferences.Editor sessioneditor;


    private SessionManager(Context context) {
        sessionsharedPreferences = context.getSharedPreferences("UserManager", Context.MODE_PRIVATE);
        sessioneditor = sessionsharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void loginUser(String userID,String username, String email, Boolean notification,String roles, String bio) {
        sessioneditor.putString("userID", userID);
        sessioneditor.putString("userName", username);
        sessioneditor.putString("email", email);
        sessioneditor.putBoolean("notification", notification);
        sessioneditor.putBoolean("UserLoggedIn", true);
        sessioneditor.putString("roleType", roles);
        sessioneditor.putString("bio", bio);
        sessioneditor.apply();
    }

    public void updateUserInfo(String username, String bio) {
        sessioneditor.putString("userName", username);
        sessioneditor.putString("bio", bio);
        sessioneditor.apply();
    }

    public String getUserID() {
        return sessionsharedPreferences.getString("userID", "");
    }
    public String getUsername() {
        return sessionsharedPreferences.getString("userName", "");
    }

    public String getEmail() {
        return sessionsharedPreferences.getString("email", "");
    }

    public  String getBio()
    {
        return sessionsharedPreferences.getString("bio","");
    }

    public boolean getNotificationStatus() {
        return sessionsharedPreferences.getBoolean("notification", false);
    }

    public void setNotificationStatus(boolean notification) {
        sessioneditor.putBoolean("notification", notification);
        sessioneditor.apply();
    }
    public String getroleType() {
        return sessionsharedPreferences.getString("roleType", "");
    }
    public boolean userLoggedIn() {
        return sessionsharedPreferences.getBoolean("UserLoggedIn", false);
    }
    public void logoutUser() {
        sessioneditor.clear();
        sessioneditor.apply();
    }

}


