package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialtrailsapp.CustomAdapter.FollowingAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class FollowingsList extends BottomMenuActivity implements FollowingAdapter.OnFollowingClickListener {

    private RecyclerView recyclerView;
    private FollowingAdapter followingAdapter;
    private List<Users> followingUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_followings_list, findViewById(R.id.container));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        followingUserList = new ArrayList<>();
        loadFollowingUsers();
    }

    private void loadFollowingUsers() {
        String currentUserId = SessionManager.getInstance(this).getUserID(); // Get current user ID

        FollowService followService = new FollowService();
        followService.getFollowingDetails(currentUserId, new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> users) {
                if (users.isEmpty()) {
                    Toast.makeText(FollowingsList.this, "No followings found.", Toast.LENGTH_SHORT).show();
                } else
                followingUserList.addAll(users);
                Log.d("FollowingsList", "Loaded following users: " + followingUserList.size());
                followingAdapter = new FollowingAdapter(followingUserList, FollowingsList.this);
                recyclerView.setAdapter(followingAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(FollowingsList.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFollowingClick(int position) {
        Users selectedUser = followingUserList.get(position);
        Intent intent = new Intent(FollowingsList.this, ViewProfileActivity.class);
        intent.putExtra("userId", selectedUser.getUserId());
        startActivity(intent);
    }
}
