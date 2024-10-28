package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.FollowersAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AdminFollowersActivity extends AdminBottomMenuActivity implements FollowersAdapter.OnFollowerClickListener {

    private RecyclerView recyclerView;
    private FollowersAdapter adapter;
    private List<Users> followersList; // List to hold followers
    private FollowService followService;
    String userId;
    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_admin_followers);
        getLayoutInflater().inflate(R.layout.activity_admin_followers, findViewById(R.id.container));
        recyclerView = findViewById(R.id.recyclerViewUsers);
        backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize followers list and FollowService
        followersList = new ArrayList<>();
        followService = new FollowService();

        // Initialize adapter with an empty list
        adapter = new FollowersAdapter( this,followersList, this);
        recyclerView.setAdapter(adapter);

        // Load followers for the specified user
         userId = getIntent().getStringExtra("userId"); // Get user ID from intent
        loadFollowers(userId);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminFollowersActivity.this, AdminUserViewActivity.class);
                intent.putExtra("intentuserId",userId);
                startActivity(intent);
            }
        });
    }

    private void loadFollowers(String userId) {
        followService.getFollowersDetails(userId, new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> followers) {
                if (followers.isEmpty()) {
                    Toast.makeText(AdminFollowersActivity.this, "No followers found.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("AdminFollowersActivity", "Loaded followers: " + followers.size());
                    followersList.addAll(followers);
                    Log.d("AdminFollowersActivity", "Loaded followers: " + followersList.size());
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminFollowersActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFollowerClick(int position) {
        Users selectedUser = followersList.get(position);
        Intent intent = new Intent(AdminFollowersActivity.this, AdminUserViewActivity.class);
        intent.putExtra("intentuserId", selectedUser.getUserId());
        startActivity(intent);
    }


    @Override
    public void onRemoveClick(int position) {
        Users userToRemove = followersList.get(position);

        followService.unfollowUser(userId, userToRemove.getUserId(), new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminFollowersActivity.this, "You have unfollowed " + userToRemove.getUsername(), Toast.LENGTH_SHORT).show();
                followersList.remove(position);
                adapter.notifyItemRemoved(position); // Update the UI
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminFollowersActivity.this, "Error unfollowing user: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
