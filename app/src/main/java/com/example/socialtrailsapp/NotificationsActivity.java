package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.NotificationAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.Utility.NotificationService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BottomMenuActivity {
    private NotificationService notificationService;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private SessionManager sessionManager;
    ImageView backButton;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_notifications);
        getLayoutInflater().inflate(R.layout.activity_notifications, findViewById(R.id.container));
        recyclerView = findViewById(R.id.recycler_view_notifications);
        backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications,this);
        recyclerView.setAdapter(adapter);
        sessionManager = SessionManager.getInstance(this);
        notificationService = new NotificationService();
        fetchNotifications();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchNotifications() {
        notificationService.fetchNotifications(sessionManager.getUserID(), new DataOperationCallback<List<Notification>>() {
            @Override
            public void onSuccess(List<Notification> data) {
                notifications.clear();
                notifications.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });

    }
}