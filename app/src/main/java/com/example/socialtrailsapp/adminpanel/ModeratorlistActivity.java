package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users; // Import the Users class
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.SessionManager; // Import SessionManager if needed
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ModeratorlistActivity extends AdminBottomMenuActivity {

    private List<Users> moderators = new ArrayList<>();
    private ModeratorAdapter adapter;
    private RecyclerView recyclerView;
    private UserService userService;
    private Button createModeratorButton;
    private SessionManager sessionManager; // SessionManager to get user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_moderatorlist, findViewById(R.id.container));

        createModeratorButton = findViewById(R.id.createModeratorButton);
        recyclerView = findViewById(R.id.recyclerViewModerators);
        adapter = new ModeratorAdapter(this, moderators);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userService = new UserService();
        sessionManager = SessionManager.getInstance(this);
        checkUserRole();
        fetchModerators();

        createModeratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ModeratorlistActivity.this, AdminCreateModeratorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void checkUserRole() {
        String userId = sessionManager.getUserID(); // Get user ID from session manager
        userService.getUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users user) {
                String role = user.getRoles();
                // Check if the user is a moderator
                if (role.equals(UserRole.MODERATOR.getRole())) {
                    createModeratorButton.setVisibility(View.GONE); // Hide button for moderators
                } else {
                    createModeratorButton.setVisibility(View.VISIBLE); // Show button for admins
                }
            }

            @Override
            public void onFailure(String errMessage) {
                Log.e("ModeratorListActivity", "Failed to retrieve user role: " + errMessage);
                createModeratorButton.setVisibility(View.VISIBLE); // Default to visible if there's an error
            }
        });
    }

    private void fetchModerators() {
        userService.getModeratorList(new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> data) {
                moderators.clear();
                moderators.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errMessage) {
                Log.e("Moderators Activity", "Error loading users: " + errMessage);
                Intent intent = new Intent(ModeratorlistActivity.this, AdminUserViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
