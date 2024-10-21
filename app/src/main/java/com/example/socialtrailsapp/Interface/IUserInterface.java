package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Users;

import java.util.List;

public interface IUserInterface {
    void createUser(Users user, OperationCallback callback);
    void getUserByID(String uid, DataOperationCallback<Users> callback);
    void setNotification(String userID, boolean isEnabled, OperationCallback callback);
    void getRegularUserList(DataOperationCallback<List<Users>> callback);
    void suspendProfile(String userId,String suspendedBy,String reason, OperationCallback callback);
    void adminGetUserByID(String uid, DataOperationCallback<Users> callback);
    void activateProfile(String userId,OperationCallback callback);
    void adminDeleteProfile(String userId,OperationCallback callback);
    void adminUnDeleteProfile(String userId,OperationCallback callback);
    void getModeratorList(DataOperationCallback<List<Users>> callback) ;
    void deleteProfile(String uid,OperationCallback callback);
    void setbackdeleteProfile(String uid);
    public void getActiveUserList(DataOperationCallback<List<Users>> callback);

    }
