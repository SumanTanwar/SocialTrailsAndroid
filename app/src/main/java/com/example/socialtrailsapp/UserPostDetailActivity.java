package com.example.socialtrailsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.PostImageAdapter;
import com.example.socialtrailsapp.CustomAdapter.UserPostAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;

import java.util.ArrayList;
import java.util.List;

public class UserPostDetailActivity extends BottomMenuActivity {
    private RecyclerView postsRecyclerView;
    private UserPostAdapter postAdapter;
    private List<UserPost> userPosts;
    private UserPostService userPostService;


    private SessionManager sessionManager;
    private String postdetailId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_user_post_detail);
        getLayoutInflater().inflate(R.layout.activity_user_post_detail, findViewById(R.id.container));

        userPostService = new UserPostService();

        Intent intent = getIntent();

        postdetailId = intent.getStringExtra("postdetailId");
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        userPosts = new ArrayList<>();

        sessionManager = SessionManager.getInstance(this);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new UserPostAdapter(this, userPosts,false);
        postsRecyclerView.setAdapter(postAdapter);
        loadPostDetails();




    }

    private void loadPostDetails() {
        String userId = sessionManager.getUserID();
        userPostService.getAllUserPostDetail(userId, new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> postList) {
                if (postList.isEmpty()) {
                    Toast.makeText(UserPostDetailActivity.this, "Post load failed! Please try again later.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserPostDetailActivity.this, ViewProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                    userPosts.clear();
                    userPosts.addAll(postList);
                    postAdapter.notifyDataSetChanged();
                if (postdetailId != null) {
                    for (int i = 0; i < userPosts.size(); i++) {
                        if (userPosts.get(i).getPostId().equals(postdetailId)) {
                            postsRecyclerView.scrollToPosition(i);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onFailure(String error) {
                Log.d("detailpage","error" + error);
                Toast.makeText(UserPostDetailActivity.this, "Post load failed! Please try again later.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserPostDetailActivity.this, ViewProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}