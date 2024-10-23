package com.example.socialtrailsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.GalleryImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.NotificationService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;

import java.util.ArrayList;
import java.util.List;

public class FollowUnfollowActivity extends BottomMenuActivity {

    private String userId;
    private UserService userService;
    private UserPostService userPostService;
    private TextView txtprofileusername, txtuserbio, postscount;
    private Button btnFollowUnfollow, btnReject, btnConfirm, btnFollowBack;
    private SessionManager sessionManager;
    private List<UserPost> list;
    private ImageView profile_pic;
    private FollowService followService;
    private NotificationService notificationService;

    private LinearLayout backsection,confirmsection,followsection; // Declare backsection

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
        btnFollowUnfollow = findViewById(R.id.btnFollowUnfollow);
        profile_pic = findViewById(R.id.profile_pic);
        btnReject = findViewById(R.id.btnReject);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnFollowBack = findViewById(R.id.btnFollowBack);
        backsection = findViewById(R.id.backsection); // Initialize backsection
        confirmsection = findViewById(R.id.confirmsection); // Initialize backsection
        followsection = findViewById(R.id.followsection); // Initialize backsection

        sessionManager = SessionManager.getInstance(this);
        followService = new FollowService();
        notificationService = new NotificationService();

        userService.getUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                setDetail(data);
                followsection.setVisibility(View.VISIBLE);
                checkPendingFollowRequests(data.getUserId());
                checkFollowBack(data.getUserId());
            }

            @Override
            public void onFailure(String errMessage) {
                Intent intent = new Intent(FollowUnfollowActivity.this, SearchUserActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(FollowUnfollowActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
        followsection.setVisibility(View.GONE);
        backsection.setVisibility(View.GONE);
        confirmsection.setVisibility(View.GONE);
        btnFollowUnfollow.setOnClickListener(v -> sendFollowRequest());
        btnConfirm.setOnClickListener(v -> confirmFollowRequest());
        btnReject.setOnClickListener(v -> rejectFollowRequest());
        btnFollowBack.setOnClickListener(v -> followBack());
    }

    private void sendFollowRequest() {
        String currentUserId = sessionManager.getUserID(); // Get current user ID
        followService.sendFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request sent!", Toast.LENGTH_SHORT).show();
                followsection.setVisibility(View.GONE); // Disable button after request
//              cancel request
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPendingFollowRequests(String userIdToCheck) {
        String currentUserId = sessionManager.getUserID();
        followService.checkPendingRequests(currentUserId, userIdToCheck, new DataOperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasPendingRequest) {
                if (hasPendingRequest) {
                    confirmsection.setVisibility(View.VISIBLE);
                    followsection.setVisibility(View.GONE); // Hide follow button
                } else {
                    followsection.setVisibility(View.VISIBLE); // Show follow button if no pending requests
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle error...
            }
        });
    }

    private void getAllUserPost(String userId) {
        userPostService.getAllUserPost(userId, new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> data) {
                list = new ArrayList<>(data);
                postscount.setText(String.valueOf(list.size()));
                List<String> imageUrls = new ArrayList<>();
                List<String> postIds = new ArrayList<>();

                for (UserPost post : list) {
                    imageUrls.add(post.getUploadedImageUris().get(0).toString());
                    postIds.add(post.getPostId());
                }

                GridView gridView = findViewById(R.id.gallery_grid);
                GalleryImageAdapter adapter = new GalleryImageAdapter(FollowUnfollowActivity.this, imageUrls, postIds);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmFollowRequest() {
        String currentUserId = sessionManager.getUserID();
        followService.confirmFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request confirmed!", Toast.LENGTH_SHORT).show();
                backsection.setVisibility(View.VISIBLE);
                confirmsection.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectFollowRequest() {
        String currentUserId = sessionManager.getUserID();
        followService.rejectFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request rejected!", Toast.LENGTH_SHORT).show();
                followsection.setVisibility(View.VISIBLE);
                confirmsection.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFollowBack(String userIdToCheck) {
        String currentUserId = sessionManager.getUserID();
        followService.checkIfFollowed(currentUserId, userIdToCheck, new DataOperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFollowing) {
                if (isFollowing) {
                    backsection.setVisibility(View.VISIBLE); // Show the back section if already following
                    followsection.setVisibility(View.GONE); // Show the back section if already following
                } else {
                    backsection.setVisibility(View.GONE); // Hide the back section if not following
                    followsection.setVisibility(View.VISIBLE); // Show follow button
                }
            }

            @Override
            public void onFailure(String error) {
                // Handle error...
            }
        });
    }

    private void followBack() {
        String currentUserId = sessionManager.getUserID();
        followService.followBack(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "You are now following this user!", Toast.LENGTH_SHORT).show();
                backsection.setVisibility(View.GONE);
                // unfollow button show
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDetail(Users user) {
        txtprofileusername.setText(user.getUsername());
        txtuserbio.setText(user.getBio());

        if (user.getProfilepicture() != null && !user.getProfilepicture().isEmpty()) {
            Uri profileImageUri = Uri.parse(user.getProfilepicture());
            Glide.with(this)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(profile_pic);
        } else {
            Glide.with(this)
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(profile_pic);
        }

        getAllUserPost(user.getUserId());
    }
}
