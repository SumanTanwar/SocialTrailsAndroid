package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.FollowersAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FollowersList extends BottomMenuActivity implements FollowersAdapter.OnFollowerClickListener {

    private RecyclerView recyclerView;
    private FollowersAdapter adapter;
    private List<Users> followersList; // List to hold followers
    private FollowService followService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_followers_list, findViewById(R.id.container));

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize followers list and FollowService
        followersList = new ArrayList<>();
        followService = new FollowService();

        // Initialize adapter with an empty list
        adapter = new FollowersAdapter(followersList, this);
        recyclerView.setAdapter(adapter);

        // Load followers for the current user
        loadFollowers();
    }

    private void loadFollowers() {
        String currentUserId = SessionManager.getInstance(this).getUserID(); // Get current user ID

        followService.getFollowersDetails(currentUserId, new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> followers) {
                if (followers.isEmpty()) {
                    Toast.makeText(FollowersList.this, "No followers found.", Toast.LENGTH_SHORT).show();
                } else {
                    followersList.addAll(followers);
                    Log.d("FollowersList", "Loaded followers: " + followersList.size());
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowersList.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFollowerClick(int position) {
        Users selectedUser = followersList.get(position);
        Intent intent = new Intent(FollowersList.this, ViewProfileActivity.class);
        intent.putExtra("userId", selectedUser.getUserId());
        startActivity(intent);
    }
}
