package com.example.socialtrailsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.example.socialtrailsapp.CustomAdapter.GalleryImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.adminpanel.AdminUserViewActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ViewProfileActivity extends BottomMenuActivity {

    Button btnEdit;
    TextView txtprofileuser,bio,postscount,followersCount, followingsCount;
    ImageView profileImageView;
    List<UserPost> list ;
    SessionManager sessionManager;
    UserService userService;
    UserPostService userPostService;
    FollowService followService;
    FirebaseAuth mAuth;
    LinearLayout followersList, followingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_view_profile, findViewById(R.id.container));

        btnEdit = findViewById(R.id.btnEditProfile);
        txtprofileuser = findViewById(R.id.txtprofileusername);
        bio = findViewById(R.id.bio);
        profileImageView = findViewById(R.id.profileImageView);
        postscount = findViewById(R.id.posts_count);
        followersCount = findViewById(R.id.followers_count);
        followingsCount = findViewById(R.id.followings_count);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();
        userPostService = new UserPostService();
        followService = new FollowService();

        followersList = findViewById(R.id.followersList);
        followingsList = findViewById(R.id.followingsList);

        if (sessionManager.userLoggedIn())
        {
            txtprofileuser.setText(sessionManager.getUsername());
            bio.setText(sessionManager.getBio());
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
            getAllUserPost(sessionManager.getUserID());
            getFollowersCount(sessionManager.getUserID());
            getFollowingCount(sessionManager.getUserID());
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                finish();

            }
        });

        followersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, FollowersList.class);
                startActivity(intent);
                finish();
            }
        });
        followingsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, FollowingsList.class);
                startActivity(intent);
                finish();
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
                    postIds.add(post.getPostId());
                }

                // Set up the GridView
                GridView gridView = findViewById(R.id.galleryProfile_grid);
                GalleryImageAdapter adapter = new GalleryImageAdapter(ViewProfileActivity.this, imageUrls,postIds);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {

                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getFollowersCount(String userId) {
        followService.getFollowersCount(userId, new DataOperationCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                followersCount.setText(String.valueOf(count));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error fetching followers count: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFollowingCount(String userId) {
        followService.getFollowingCount(userId, new DataOperationCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                followingsCount.setText(String.valueOf(count));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error fetching following count: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}