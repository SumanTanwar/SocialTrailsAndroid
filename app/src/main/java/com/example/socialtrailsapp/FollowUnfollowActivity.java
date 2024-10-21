package com.example.socialtrailsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.GalleryImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.UserFollow;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowUnfollowActivity extends BottomMenuActivity {

    String userId;
    UserService userService;
    UserPostService userPostService;
    TextView txtprofileusername, txtuserbio, postscount;
    Button btnFollowUnfollow;
    private SessionManager sessionManager;
    List<UserPost> list;
    ImageView profile_pic;
    FollowService followService; // Declare FollowService

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
        sessionManager = SessionManager.getInstance(this);
        followService = new FollowService(this); // Initialize FollowService

        userService.adminGetUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                setDetail(data);
                checkFollowingStatus(sessionManager.getUserID(), userId);
            }

            @Override
            public void onFailure(String errMessage) {
                Intent intent = new Intent(FollowUnfollowActivity.this, SearchUserActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(FollowUnfollowActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        btnFollowUnfollow.setOnClickListener(v -> toggleFollow(sessionManager.getUserID(), userId));
    }

    private void checkFollowingStatus(String followersId, String userId) {
        DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("userfollow");
        followRef.orderByChild("followersId").equalTo(followersId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isFollowing = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserFollow userFollow = snapshot.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getUserId().equals(userId)) {
                                isFollowing = true;
                                break;
                            }
                        }
                        btnFollowUnfollow.setText(isFollowing ? "Unfollow" : "Follow");
                        Log.d("FollowCheck", "Is following: " + isFollowing);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(FollowUnfollowActivity.this, "Error checking follow status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleFollow(String followersId, String userId) {
        DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("userfollow");
        followRef.orderByChild("followersId").equalTo(followersId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isFollowing = false;
                        String existingFollowKey = null;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserFollow userFollow = snapshot.getValue(UserFollow.class);
                            if (userFollow != null && userFollow.getUserId().equals(userId)) {
                                isFollowing = true;
                                existingFollowKey = snapshot.getKey();
                                break;
                            }
                        }

                        if (isFollowing) {
                            // Unfollow
                            if (existingFollowKey != null) {
                                followService.removeFollow(existingFollowKey);
                                btnFollowUnfollow.setText("Follow");
                            }
                        } else {
                            // Follow
                            UserFollow userFollow = new UserFollow();
                            userFollow.setUserId(userId);
                            userFollow.setFollowersId(followersId);
                            userFollow.setFollowingId(followersId); // Set to the follower's ID if needed
                            followService.addFollow(userFollow);
                            btnFollowUnfollow.setText("Unfollow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(FollowUnfollowActivity.this, "Error toggling follow status: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getAllUserPost(String userId) {
        userPostService.getAllUserPost(userId, new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> data) {
                list = new ArrayList<>(data);
                Log.d("Post count", "count: " + list.size());
                postscount.setText(String.valueOf(list.size()));
                List<String> imageUrls = new ArrayList<>();
                for (UserPost post : list) {
                    imageUrls.add(post.getUploadedImageUris().get(0).toString());
                }

                GridView gridView = findViewById(R.id.gallery_grid);
                GalleryImageAdapter adapter = new GalleryImageAdapter(FollowUnfollowActivity.this, imageUrls, new ArrayList<>());
                gridView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDetail(Users user) {
        txtprofileusername.setText(user.getUsername());

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
