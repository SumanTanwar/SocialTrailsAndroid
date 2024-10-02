package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.UserService;

import java.util.ArrayList;
import java.util.List;

public class AdminListofUsersActivity extends AdminBottomMenuActivity {

    private RecyclerView recyclerViewUsers;
    private AdminUserAdapter adminUserAdapter;
    private List<Users> usersList;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for the activity
        getLayoutInflater().inflate(R.layout.activity_admin_listof_users, findViewById(R.id.container));

        // Initialize the RecyclerView
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list and adapter
        usersList = new ArrayList<>();
        adminUserAdapter = new AdminUserAdapter(usersList); // Updated constructor call
        recyclerViewUsers.setAdapter(adminUserAdapter);

        // Initialize UserService
        userService = new UserService();

        // Load user list
        loadUserList();
    }

    private void loadUserList() {
        userService.getRegularUserList(new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> data) {
                usersList.clear();
                usersList.addAll(data);
                adminUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errMessage) {
                Log.e("AdminListofUsersActivity", "Error loading users: " + errMessage);
                // Handle error
            }
        });
    }
}
