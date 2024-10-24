package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.CustomAdapter.SearchUserAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends BottomMenuActivity implements SearchUserAdapter.OnTextClickListener {

    private RecyclerView recyclerViewUsers;
    private SearchUserAdapter searchUserAdapter;
    private List<Users> usersList;
    private UserService userService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_search_user, findViewById(R.id.container));

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        usersList = new ArrayList<>();
        searchUserAdapter = new SearchUserAdapter(usersList, this);
        recyclerViewUsers.setAdapter(searchUserAdapter);

        userService = new UserService();
        sessionManager = SessionManager.getInstance(this);

        loadUserList();
    }

    private void loadUserList() {
        userService.getActiveUserList(new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> data) {
                String currentUserId = sessionManager.getUserID();
                usersList.clear();

                for (Users user : data) {
                    if (!user.getUserId().equals(currentUserId)) {
                        usersList.add(user);
                    }
                }

                searchUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errMessage) {
                Log.e("SearchUserActivity", "Error loading users: " + errMessage);
            }
        });
    }

    @Override
    public void redirectToProfilePage(int position) {
        Users user = usersList.get(position);
        Intent intent = new Intent(SearchUserActivity.this, FollowUnfollowActivity.class);
        intent.putExtra("intentuserId", user.getUserId());
        startActivity(intent);
    }

}
