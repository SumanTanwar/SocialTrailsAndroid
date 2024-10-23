package com.example.socialtrailsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.UserPostAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BottomMenuActivity {
    private SessionManager sessionManager;
    TextView txtmains;
    private RecyclerView postsRecyclerView;
    private UserPostAdapter postAdapter;
    private List<UserPost> userPosts;
    private UserPostService userPostService;
    ImageView profileImageView,btnNotify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.container));

        txtmains = findViewById(R.id.userNameTextView);
        sessionManager = SessionManager.getInstance(this);
        btnNotify = findViewById(R.id.btnNotify);
        profileImageView = findViewById(R.id.profileImageView);

        Glide.with(this)
                .load(R.drawable.user)
                .transform(new CircleCrop())
                .into(profileImageView);

        if (sessionManager.userLoggedIn())
        {
            txtmains.setText(sessionManager.getUsername());

            if (sessionManager.getProfileImage() != null) {
                Uri profileImageUri = Uri.parse(sessionManager.getProfileImage());
                Glide.with(this)
                        .load(profileImageUri)
                        .transform(new CircleCrop())
                        .into(profileImageView);

            } else {
                Glide.with(this)
                        .load(R.drawable.user)
                        .transform(new CircleCrop())
                        .into(profileImageView);

            }

        }



        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowNotificationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userPostService = new UserPostService();



        postsRecyclerView = findViewById(R.id.dashpostsRecyclerView);
        userPosts = new ArrayList<>();
        sessionManager = SessionManager.getInstance(this);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new UserPostAdapter(this, userPosts);
        postsRecyclerView.setAdapter(postAdapter);
        loadPostDetails();

    }

    private void loadPostDetails() {
        userPostService.retrievePostsForFollowedUsers(sessionManager.getUserID(), new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> postList) {
                Log.d("postimage","in main page " + postList.size());
                userPosts.clear();
                userPosts.addAll(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Log.d("postimage","in main page error " + error);
            }
        });
    }
}