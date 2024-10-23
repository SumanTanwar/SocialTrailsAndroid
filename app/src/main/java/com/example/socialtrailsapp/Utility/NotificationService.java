package com.example.socialtrailsapp.Utility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.ModelData.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NotificationService {

    private DatabaseReference reference;
    private static final String _collectionName = "notifications";
    private StorageReference storageReference;

    public NotificationService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void sendNotificationToUser(String userId, Notification notification) {
        DatabaseReference notificationRef = reference.child(_collectionName).child(userId);
        String notificationId = notificationRef.push().getKey();

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

    public void deleteNotificationForUser(String userId, String followerId) {
        DatabaseReference notificationRef = reference.child(_collectionName).child(userId);

        notificationRef.orderByChild("followerId").equalTo(followerId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid ->
                                                Log.d("Notification", "Notification deleted successfully."))
                                        .addOnFailureListener(e ->
                                                Log.e("Notification", "Failed to delete notification: " + e.getMessage()));
                            }
                        } else {
                            Log.d("Notification", "No notifications found for the given follower ID.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Notification", "Error deleting notification: " + databaseError.getMessage());
                    }
                });
    }
}
