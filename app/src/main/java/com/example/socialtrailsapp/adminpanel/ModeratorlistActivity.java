package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.Users; // Import the Users class
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModeratorlistActivity extends AdminBottomMenuActivity {

    private List<Users> moderators = new ArrayList<>();
    private ModeratorAdapter adapter;
    private RecyclerView recyclerView;
    private UserService userService;
    Button createModeratorButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        getLayoutInflater().inflate(R.layout.activity_moderatorlist, findViewById(R.id.container));

        createModeratorButton = findViewById(R.id.createModeratorButton);
        recyclerView = findViewById(R.id.recyclerViewModerators);
        adapter = new ModeratorAdapter(this, moderators);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userService = new UserService();
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
