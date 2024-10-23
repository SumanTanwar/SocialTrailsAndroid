package com.example.socialtrailsapp.Utility;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserFollow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FollowService {

    private DatabaseReference reference;
    private static String _collectionName = "userfollow";

    public FollowService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
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

    public void followBack(String currentUserId, String userIdToFollow, OperationCallback callback) {
        reference.child(_collectionName)
                .orderByChild("userId")
                .equalTo(userIdToFollow)
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
                                            callback.onSuccess();
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
}
