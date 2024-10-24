package com.example.socialtrailsapp.Utility;

import android.util.Log;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.IFollowService;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserFollow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class FollowService implements IFollowService {

    private DatabaseReference reference;
    private static String _collectionName = "userfollow";

    public FollowService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
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
    @Override
    public void checkPendingRequests(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
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
    @Override
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
    @Override
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
    @Override
    public void followBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("userId").equalTo(userIdToFollow)
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
                                            // Confirm the follow back in the current user's following list
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

    private void confirmFollowBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
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

    @Override
    public void checkIfFollowed(String currentUserId, String userIdToCheck, DataOperationCallback<Boolean> callback) {
        reference.child("userfollow")
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
    @Override
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
                                Log.d("followers", "followerId : " + userFollow.getFollowerIds());
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


}
