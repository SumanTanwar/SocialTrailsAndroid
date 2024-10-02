package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Users;

import java.util.List;

public interface IUserInterface {
    void createUser(Users user, OperationCallback callback);
    void getUserByID(String uid, DataOperationCallback<Users> callback);
    void setNotification(String userID, boolean isEnabled, OperationCallback callback);
    public void setbackdeleteProfile(String userID);
    public void deleteProfile(String userID, OperationCallback callback);
    void getRegularUserList(DataOperationCallback<List<Users>> callback);

}
