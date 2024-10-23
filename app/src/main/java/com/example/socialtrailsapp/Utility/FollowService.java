package com.example.socialtrailsapp.Utility;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.ModelData.UserFollow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FollowService {
    private DatabaseReference databaseReference;
    //private Context context;

    public FollowService() {

        databaseReference = FirebaseDatabase.getInstance().getReference("userfollow");
    }
    public void addFollow(UserFollow userFollow, OperationCallback callback) {
        userFollow.setCreatedOn(Utils.getCurrentDatetime());
        String followId = databaseReference.push().getKey();
        userFollow.setFollowId(followId);

        databaseReference.child(followId).setValue(userFollow)
                .addOnSuccessListener(aVoid -> {

                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {

                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }

    public void removeFollow(String followId, OperationCallback callback) {
        databaseReference.child(followId).removeValue()
                .addOnSuccessListener(aVoid -> {

                    if (callback != null) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {

                    if (callback != null) {
                        callback.onFailure(e.getMessage());
                    }
                });
    }


    public void getPostsFromFollowedUsers(String currentUserId, final DataOperationCallback<List<String>> callback) {
        List<String> followedUserIds = new ArrayList<>();

        // First, get the IDs of followed users
        databaseReference.orderByChild("userId").equalTo(currentUserId)
          .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String followingId = snapshot.child("followingId").getValue(String.class);
                    if (followingId != null) {
                        followedUserIds.add(followingId);
                    }
                }
                callback.onSuccess(followedUserIds);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void sendNotificationToUser(String userId, Notification notification) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);
        String notificationId = notificationRef.push().getKey();
        if (notificationId != null) {
            notificationRef.child(notificationId).setValue(notification)
                    .addOnSuccessListener(aVoid -> Log.d("Notification", "Notification sent successfully."))
                    .addOnFailureListener(e -> Log.e("Notification", "Failed to send notification: " + e.getMessage()));
        }
    }

    public void deleteNotificationForUser(String userId, String followerId) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

        notificationRef.orderByChild("followerId").equalTo(followerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            snapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("Notification", "Notification deleted successfully."))
                                    .addOnFailureListener(e ->
                                            Log.e("Notification", "Failed to delete notification: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Notification", "Error deleting notification: " + databaseError.getMessage());
                    }
                });
    }

}
