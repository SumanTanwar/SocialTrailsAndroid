package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.INotification;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.ModelData.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationService implements INotification {

    private DatabaseReference reference;
    private static final String _collectionName = "notifications";
    private StorageReference storageReference;
    private UserService userService;
    public NotificationService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        userService = new UserService();
    }

    public void sendNotificationToUser(Notification notification) {
        DatabaseReference notificationRef = reference.child(_collectionName);
        String notificationId = notificationRef.push().getKey();
        notification.setNotificationId(notificationId);
        if (notificationId != null) {
            notificationRef.child(notificationId).setValue(notification)
                    .addOnSuccessListener(aVoid ->
                            Log.d("Notification", "Notification sent successfully."))
                    .addOnFailureListener(e ->
                            Log.e("Notification", "Failed to send notification: " + e.getMessage()));
        } else {
            Log.e("Notification", "Failed to generate notification ID.");
        }
    }
    public void fetchNotifications(String userId, final DataOperationCallback<List<Notification>> callback) {
        DatabaseReference notificationRef = reference.child(_collectionName);

        // Query to filter by userId and order by createdon
        notificationRef.orderByChild("notifyto").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Notification> notifications = new ArrayList<>();
                        AtomicInteger userFetchCount = new AtomicInteger(0);
                        int totalNotifications = (int) dataSnapshot.getChildrenCount();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Notification notification = snapshot.getValue(Notification.class);
                            if (notification != null) {
                                // Fetch user details for each notification
                                retrieveUserDetails(notification.getNotifyBy(), new DataOperationCallback<Users>() {
                                    @Override
                                    public void onSuccess(Users userDetails) {
                                        notification.setUsername(userDetails.getUsername());
                                        notification.setUserprofilepicture(userDetails.getProfilepicture());
                                        notifications.add(notification);

                                        // Check if all notifications have been processed
                                        if (userFetchCount.incrementAndGet() == totalNotifications) {
                                            // Sort notifications by createdon timestamp
                                            Collections.sort(notifications, (n1, n2) -> n2.getCreatedon().compareTo(n1.getCreatedon()));
                                            callback.onSuccess(notifications);
                                        }
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Log.e("NotificationService", "Failed to fetch user details: " + error);
                                        notifications.add(notification); // Add notification even if user details fail

                                        // Check if all notifications have been processed
                                        if (userFetchCount.incrementAndGet() == totalNotifications) {
                                            // Sort notifications by createdon timestamp
                                            Collections.sort(notifications, (n1, n2) -> n2.getCreatedon().compareTo(n1.getCreatedon()));
                                            callback.onSuccess(notifications);
                                        }
                                    }
                                });
                            }
                        }

                        // Handle case where there are no notifications
                        if (totalNotifications == 0) {
                            callback.onSuccess(notifications);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("NotificationService", "Error fetching notifications: " + databaseError.getMessage());
                        callback.onFailure(databaseError.toException().getMessage());
                    }
                });
    }



    private void retrieveUserDetails(String userId, DataOperationCallback<Users> callback) {

        userService.getUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                callback.onSuccess(data);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure("User not found");
            }
        });


    }


}
