package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.FollowingAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AdminFollowingsActivity extends AdminBottomMenuActivity implements FollowingAdapter.OnFollowingClickListener {

    private RecyclerView recyclerView;
    private FollowingAdapter adapter;
    private List<Users> followingList; // List to hold followings
    private FollowService followService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_followings);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize following list and FollowService
        followingList = new ArrayList<>();
        followService = new FollowService();

        // Initialize adapter with an empty list
        adapter = new FollowingAdapter(followingList, this);
        recyclerView.setAdapter(adapter);

        // Load followings for the specified user
        String userId = getIntent().getStringExtra("userId"); // Get user ID from intent
        loadFollowings(userId);
    }

    private void loadFollowings(String userId) {
        followService.getFollowingDetails(userId, new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> followings) {
                if (followings.isEmpty()) {
                    Toast.makeText(AdminFollowingsActivity.this, "No followings found.", Toast.LENGTH_SHORT).show();
                } else {
                    followingList.addAll(followings);
                    Log.d("AdminFollowingsActivity", "Loaded followings: " + followingList.size());
                    adapter.notifyDataSetChanged(); // Notify the adapter of data changes
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminFollowingsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFollowingClick(int position) {
        Users selectedUser = followingList.get(position);
        Intent intent = new Intent(AdminFollowingsActivity.this, AdminUserViewActivity.class);
        intent.putExtra("userId", selectedUser.getUserId());
        startActivity(intent);
    }

    @Override
    public void onRemoveClick(int position) {
        Users userToRemove = followingList.get(position);
        String currentUserId = SessionManager.getInstance(this).getUserID(); // Get the admin's ID

        followService.unfollowUser(currentUserId, userToRemove.getUserId(), new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminFollowingsActivity.this, "You have removed " + userToRemove.getUsername(), Toast.LENGTH_SHORT).show();
                followingList.remove(position);
                adapter.notifyItemRemoved(position); // Update the UI
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminFollowingsActivity.this, "Error removing user: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onUnfollowClick(int position) {
        // Handle the remove action
        onUnfollowClick(position);
    }

}
