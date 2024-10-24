package com.example.socialtrailsapp.Utility;

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

import java.util.ArrayList;
import java.util.List;

public class FollowService implements IFollowService {

    private DatabaseReference reference;
    private static final String _collectionName = "userfollow"; // Use final for constants
    private UserService userService;

    public FollowService() {
        reference = FirebaseDatabase.getInstance().getReference();
        userService = new UserService();
    }

    @Override
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
                            return; // Exit after processing
                        }
                    }
                } else {
                    // No existing follow records; create a new one
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

    @Override
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

    @Override
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

    @Override
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

    public void checkPendingFollowRequest(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child("follow_requests").orderByChild("requesterId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isPending = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String targetUserId = snapshot.child("requestedId").getValue(String.class);
                            if (targetUserId != null && targetUserId.equals(userIdToCheck)) {
                                isPending = true;
                                break;
                            }
                        }
                        callback.onSuccess(isPending);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onFailure("Failed to check pending follow request: " + databaseError.getMessage());
                    }
                });
    }



    @Override
    public void confirmFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userIdToFollow)
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

    @Override
    public void rejectFollowRequest(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userIdToFollow)
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

    @Override
    public void followBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userIdToFollow)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
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
                                    return;
                                }
                            }
                        } else {
                            callback.onFailure("User not found to follow back.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error checking user: " + error.getMessage());
                    }
                });
    }

    @Override
    public void confirmFollowBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                UserFollow userFollow = ds.getValue(UserFollow.class);
                                if (userFollow != null) {
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

    @Override
    public void checkIfFollowed(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child("userfollow").orderByChild("userId").equalTo(userIdToCheck)
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

    @Override
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

    @Override
    public void getFollowingDetails(String userId, DataOperationCallback<List<Users>> callback) {
        reference.child(_collectionName).orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> followingIds = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserFollow userFollow = ds.getValue(UserFollow.class);
                            if (userFollow != null) {
                                followingIds.addAll(userFollow.getFollowingIds().keySet());
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

    @Override
    public void fetchUserDetails(List<String> userIds, DataOperationCallback<List<Users>> callback) {
        List<Users> userDetails = new ArrayList<>();
        if (userIds.isEmpty()) {
            callback.onSuccess(userDetails); // Return empty list if no IDs
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
                                count += userFollow.getFollowingIds().size(); // Count following
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
