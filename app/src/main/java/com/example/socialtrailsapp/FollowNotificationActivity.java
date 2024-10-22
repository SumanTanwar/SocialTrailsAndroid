package com.example.socialtrailsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.NotificationAdapter;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowNotificationActivity extends BottomMenuActivity {

    private RecyclerView recyclerViewNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_follow_notification, findViewById(R.id.container));

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList, this);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        sessionManager = SessionManager.getInstance(this);
        loadNotifications();
    }

    private void loadNotifications() {
        String currentUserId = sessionManager.getUserID();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(currentUserId);
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        notificationList.add(notification);
                    }
                }
                notificationAdapter.notifyDataSetChanged(); // Update the adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "Error fetching notifications: " + databaseError.getMessage());
            }
        });
    }


}
