package com.example.socialtrailsapp.Interface;

import java.util.List;

public interface IFollowService {
    void sendFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback);
    void checkPendingRequests(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback);
    void confirmFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback);
    void rejectFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback);
    void followBack(String currentUserId, String userIdToFollow, OperationCallback callback);
    void checkIfFollowed(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback);
    void getFollowAndFollowerIdsByUserId(String userId, DataOperationCallback<List<String>> callback);
}
