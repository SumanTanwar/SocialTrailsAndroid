package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Users;

public interface IUserInterface {
    void createUser(Users user, OperationCallback callback);
    void getUserByID(String uid, DataOperationCallback<Users> callback);
}
