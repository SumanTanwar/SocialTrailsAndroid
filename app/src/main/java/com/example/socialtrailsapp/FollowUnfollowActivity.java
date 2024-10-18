package com.example.socialtrailsapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.GalleryImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;

import java.util.ArrayList;
import java.util.List;

public class FollowUnfollowActivity extends BottomMenuActivity {

    String userId;
    UserService userService;
    UserPostService userPostService;
    TextView txtprofileusername, txtuserbio, postscount;
    private SessionManager sessionManager;
    List<UserPost> list;
    ImageView profile_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_follow_unfollow, findViewById(R.id.container));

        userId = getIntent().getStringExtra("intentuserId");
        userService = new UserService();
        userPostService = new UserPostService();
        txtprofileusername = findViewById(R.id.txtprofileusername);
        txtuserbio = findViewById(R.id.txtuserbio);
        postscount = findViewById(R.id.userpostscount);
        profile_pic = findViewById(R.id.profile_pic);
        sessionManager = SessionManager.getInstance(this);

        userService.adminGetUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                setDetail(data);
            }

            @Override
            public void onFailure(String errMessage) {
                Intent intent = new Intent(FollowUnfollowActivity.this, SearchUserActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(FollowUnfollowActivity.this, "something wrong ! please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void getAllUserPost(String userId)
    {
        userPostService.getAllUserPost(userId, new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> data) {
                list = new ArrayList<>(data);
                Log.d("Post count","count: " + list.size());
                int size = list.size();
                postscount.setText("" + size);
                List<String> imageUrls = new ArrayList<>();
                List<String> postIds = new ArrayList<>();
                for (UserPost post : list) {
                    imageUrls.add(post.getUploadedImageUris().get(0).toString());
                }

                // Set up the GridView
                GridView gridView = findViewById(R.id.gallery_grid);
                GalleryImageAdapter adapter = new GalleryImageAdapter(FollowUnfollowActivity.this, imageUrls,postIds);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {

                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setDetail(Users user)
    {
        txtprofileusername.setText(user.getUsername());

        if (user.getSuspended()) {

        }
        if(user.getProfiledeleted())
        {

        }

        if (user.getProfilepicture() != null &&  !user.getProfilepicture().isEmpty()) {
            Uri profileImageUri = Uri.parse(user.getProfilepicture()); // Convert String to Uri
            Glide.with(this)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(profile_pic);
        } else {
            Glide.with(this)
                    .load(R.drawable.user) // Replace with your image URI or resource
                    .transform(new CircleCrop())
                    .into(profile_pic);
        }
        getAllUserPost(user.getUserId());
    }


    }

