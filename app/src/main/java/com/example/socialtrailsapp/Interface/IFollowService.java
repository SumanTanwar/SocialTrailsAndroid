
package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Users;

import java.util.List;

public interface IFollowService {
    void sendFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback);

    void cancelFollowRequest(String currentUserId, String userIdToUnfollow, OperationCallback callback);

    void unfollowUser(String currentUserId, String userIdToUnfollow, OperationCallback callback);

//    void checkPendingRequests(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback);

    void checkUserFollowStatus(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback);

    void confirmFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback);
    void rejectFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback);
    void followBack(String currentUserId, String userIdToFollow, OperationCallback callback);

    void confirmFollowBack(String currentUserId, String userIdToFollow, OperationCallback callback);

    void checkIfFollowed(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback);

    void getFollowersDetails(String userId, DataOperationCallback<List<Users>> callback);

    void getFollowingDetails(String userId, DataOperationCallback<List<Users>> callback);

    void fetchUserDetails(List<String> userIds, DataOperationCallback<List<Users>> callback);
    // void getFollowAndFollowerIdsByUserId(String userId, DataOperationCallback<List<String>> callback);
}
