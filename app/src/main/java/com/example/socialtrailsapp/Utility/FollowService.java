package com.example.socialtrailsapp.Utility;

import android.util.Log;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IFollowService;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserFollow;
import com.example.socialtrailsapp.ModelData.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class FollowService implements IFollowService {

    private DatabaseReference reference;
    private static String _collectionName = "userfollow";
    UserService userService;

    public FollowService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        userService = new UserService();
    }

    public void sendFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback) {
        DatabaseReference followRef = reference.child(_collectionName);
        followRef.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        UserFollow userFollow = ds.getValue(UserFollow.class);
                        if (userFollow != null) {
                            userFollow.addFollowingId(userIdToFollow, false);
                            ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    callback.onSuccess();
                                } else {
                                    callback.onFailure("Failed to update follow request.");
                                }
                            });
                            return;
                        }
                    }
                } else {
                    String followId = followRef.push().getKey();
                    UserFollow newUserFollow = new UserFollow(currentUserId);
                    newUserFollow.setFollowId(followId);
                    newUserFollow.addFollowingId(userIdToFollow, false);
                    followRef.child(followId).setValue(newUserFollow)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    callback.onSuccess();
                                } else {
                                    callback.onFailure("Failed to send follow request.");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure("Error checking follow requests: " + error.getMessage());
            }
        });
    }

    public void checkPendingRequestsForCancel(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child(_collectionName)
                .orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasPendingRequest = false;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getFollowingIds().containsKey(userIdToCheck)
                                    && !userFollow.getFollowingIds().get(userIdToCheck)) {
                                hasPendingRequest = true;
                                break;
                            }
                        }
                        callback.onSuccess(hasPendingRequest);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching data");
                    }
                });
    }
    public void cancelFollowRequest(String currentUserId, String userIdToUnfollow, OperationCallback callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getFollowingIds().containsKey(userIdToUnfollow)) {
                                userFollow.getFollowingIds().remove(userIdToUnfollow);
                                ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure("Failed to delete follow request.");
                                    }
                                });
                                return; // Exit after deleting
                            }
                        }
                        callback.onFailure("No follow request found to cancel.");
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching data: " + error.getMessage());
                    }
                });
    }
    public void checkPendingforFollowingUser(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child(_collectionName)
                .orderByChild("userId").equalTo(userIdToCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasPendingRequest = false;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getFollowingIds().containsKey(currentUserId)
                                    && !userFollow.getFollowingIds().get(currentUserId)) {
                                hasPendingRequest = true;
                                break;
                            }
                        }
                        callback.onSuccess(hasPendingRequest);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching data");
                    }
                });
    }
    public void confirmFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("userId").equalTo(userIdToFollow)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                userFollow.getFollowingIds().put(currentUserId, true);
                                ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure("Failed to confirm follow request.");
                                    }
                                });
                                return;
                            }
                        }
                        callback.onFailure("Follow request not found.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching data");
                    }
                });
    }
    public void rejectFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("userId").equalTo(userIdToFollow)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                userFollow.getFollowingIds().remove(currentUserId);
                                ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure("Failed to reject follow request.");
                                    }
                                });
                                return;
                            }
                        }
                        callback.onFailure("Follow request not found.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching data");
                    }
                });
    }
    public void confirmFollowBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                UserFollow userFollow = ds.getValue(UserFollow.class);
                                if (userFollow != null) {
                                    // Update the following IDs
                                    userFollow.getFollowingIds().put(userIdToFollow, true);
                                    ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            callback.onSuccess(); // Follow back confirmed
                                        } else {
                                            callback.onFailure("Failed to confirm follow back.");
                                        }
                                    });
                                    return;
                                }
                            }
                        } else {
                            // Create a new UserFollow entry if none exists
                            String followId = reference.child(_collectionName).push().getKey();
                            UserFollow newUserFollow = new UserFollow(currentUserId);
                            newUserFollow.setFollowId(followId);
                            newUserFollow.addFollowingId(userIdToFollow, true); // Add the follow ID
                            reference.child(_collectionName).child(followId).setValue(newUserFollow)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            callback.onSuccess(); // Follow back confirmed with new entry
                                        } else {
                                            callback.onFailure("Failed to create follow back entry.");
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error checking user: " + error.getMessage());
                    }
                });
    }
    public void followBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("userId")
                .equalTo(userIdToFollow)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User exists, update follower list
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                UserFollow userFollow = ds.getValue(UserFollow.class);
                                if (userFollow != null) {
                                    userFollow.addFollowerId(currentUserId);
                                    ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            confirmFollowBack(currentUserId, userIdToFollow, callback);
                                        } else {
                                            callback.onFailure("Failed to add follower.");
                                        }
                                    });
                                    return; // Exit after processing the user
                                }
                            }
                        } else {
                            // User does not exist, create a new UserFollow instance
                            UserFollow newUserFollow = new UserFollow(userIdToFollow);
                            newUserFollow.addFollowerId(currentUserId);

                            // Set the followId as a new unique key
                            String newFollowId = reference.child(_collectionName).push().getKey();
                            if (newFollowId != null) {
                                newUserFollow.setFollowId(newFollowId);
                                reference.child(_collectionName).child(newFollowId).setValue(newUserFollow)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onFailure("Failed to create follow relationship.");
                                            }
                                        });
                            } else {
                                callback.onFailure("Failed to generate unique follow ID.");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error checking user: " + error.getMessage());
                    }
                });
    }



    public void checkIfFollowed(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child(_collectionName)
                .orderByChild("userId")
                .equalTo(userIdToCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isFollowed = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserFollow userFollow = snapshot.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getFollowingIds().containsKey(currentUserId)) {
                                isFollowed = true;
                                break;
                            }
                        }
                        callback.onSuccess(isFollowed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailure("Failed to check follow status.");
                    }
                });
    }

    public void getFollowAndFollowerIdsByUserId(String userId, DataOperationCallback<List<String>> callback) {
        Set<String> allIds = new HashSet<>(); // Use a Set to ensure uniqueness

        reference.child(_collectionName)
                .orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                // Add all following IDs where the value is true
                                for (Map.Entry<String, Boolean> entry : userFollow.getFollowingIds().entrySet()) {
                                    if (entry.getValue()) {
                                        allIds.add(entry.getKey());
                                        Log.d("followers", "following id : " + entry.getKey());
                                    }
                                }

                                // Add all follower IDs
                                Log.d("followers", "followerId : " + userFollow.getFollowingIds());
                                allIds.addAll(userFollow.getFollowerIds()); // Add all follower IDs
                            }
                        }

                        Log.d("followers", "following id " + allIds);
                        callback.onSuccess(new ArrayList<>(allIds)); // Convert Set back to List before returning
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching follow and follower IDs: " + error.getMessage());
                    }
                });
    }
  
  




    public void unfollowUser(String currentUserId, String userIdToUnfollow, OperationCallback callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getFollowingIds().containsKey(userIdToUnfollow)) {
                                userFollow.getFollowingIds().remove(userIdToUnfollow);
                                ds.getRef().setValue(userFollow).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure("Failed to unfollow user.");
                                    }
                                });
                                return; // Exit after processing the user
                            }
                        }
                        callback.onFailure("User not found in following list.");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching data: " + error.getMessage());
                    }
                });
    }
  
  public void checkUserFollowStatus(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userIdToCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isFollowed = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserFollow userFollow = snapshot.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getFollowingIds().containsKey(currentUserId)) {
                                isFollowed = true;
                                break;
                            }
                        }
                        callback.onSuccess(isFollowed);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailure("Failed to check follow status.");
                    }
                });
    }

    public void getFollowersDetails(String userId, DataOperationCallback<List<Users>> callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> followerIds = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                followerIds.addAll(userFollow.getFollowerIds());
                            }
                        }
                        fetchUserDetails(followerIds, callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching followers: " + error.getMessage());
                    }
                });
    }

    public void getFollowingDetails(String userId, DataOperationCallback<List<Users>> callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> followingIds = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                // Add only those IDs where the value is true
                                for (Map.Entry<String, Boolean> entry : userFollow.getFollowingIds().entrySet()) {
                                    if (entry.getValue()) { // Check if the value is true
                                        followingIds.add(entry.getKey()); // Add the user ID
                                    }
                                }
                            }
                        }
                        fetchUserDetails(followingIds, callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching following details: " + error.getMessage());
                    }
                });
    }


    public void fetchUserDetails(List<String> userIds, DataOperationCallback<List<Users>> callback) {
        List<Users> userDetails = new ArrayList<>();

        if (userService == null) {
            callback.onFailure("UserService is not initialized.");
            return;
        }

        int totalUsers = userIds.size();
        final int[] fetchedCount = {0}; // Track fetched users

        for (String userId : userIds) {
            userService.getUserByID(userId, new DataOperationCallback<Users>() {
                @Override
                public void onSuccess(Users user) {
                    userDetails.add(user);
                    fetchedCount[0]++;
                    if (fetchedCount[0] == totalUsers) {
                        callback.onSuccess(userDetails); // All users fetched
                    }
                }

                @Override
                public void onFailure(String error) {
                    fetchedCount[0]++;
                    // Log the error but don't break the loop
                    if (fetchedCount[0] == totalUsers) {
                        callback.onSuccess(userDetails); // Return whatever was fetched
                    }
                }
            });
        }
    }

    public void getFollowersCount(String userId, DataOperationCallback<Integer> callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                count += userFollow.getFollowerIds().size(); // Count followers
                            }
                        }
                        callback.onSuccess(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching followers count: " + error.getMessage());
                    }
                });
    }

    public void getFollowingCount(String userId, DataOperationCallback<Integer> callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                // Iterate through following IDs and count those that are true
                                for (Boolean isFollowing : userFollow.getFollowingIds().values()) {
                                    if (isFollowing) {
                                        count++; // Only count if the value is true
                                    }
                                }
                            }
                        }
                        callback.onSuccess(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching following count: " + error.getMessage());
                    }
                });
    }


}