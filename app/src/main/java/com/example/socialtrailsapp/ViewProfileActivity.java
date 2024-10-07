package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;

public class ViewProfileActivity extends BottomMenuActivity {

    Button btnEdit;
    TextView txtprofileuser,bio;
    ImageView profileImageView;
    SessionManager sessionManager;
    UserService userService;
    FirebaseAuth mAuth;

    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_view_profile, findViewById(R.id.container));

        btnEdit = findViewById(R.id.btnEditProfile);
        txtprofileuser = findViewById(R.id.txtprofileusername);
        bio = findViewById(R.id.bio);
        profileImageView = findViewById(R.id.profileImageView);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();

        if (sessionManager.userLoggedIn())
        {
            txtprofileuser.setText(sessionManager.getUsername());
            bio.setText(sessionManager.getBio());
            loadProfileImage();
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("name", sessionManager.getUsername());
                intent.putExtra("email",mAuth.getCurrentUser().getEmail());
                intent.putExtra("bio", sessionManager.getBio());
                intent.putExtra("image",sessionManager.getProfileImage());
                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {

            if (data != null) {
                String newName = data.getStringExtra("name");
                if (newName != null) {
                    txtprofileuser.setText(newName);
                }
                String newBio = data.getStringExtra("bio");
                if (newBio != null) {
                    bio.setText(newBio);
                    sessionManager.updateUserInfo(newName, newBio);
                }
                String profileImage = data.getStringExtra("profileImage"); // Get the image URL
                if (profileImage != null) {
                    loadProfileImage(profileImage); // Load the new image
                }
            }
        }
    }

    private void loadProfileImage() {
        String imageUrl = sessionManager.getProfileImage(); // Assume you save the image URL in session manager
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .transform(new CircleCrop())
                    .into(profileImageView);
        }
    }


    private void loadProfileImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .transform(new CircleCrop())
                .into(profileImageView); // Load the new circular image

        sessionManager.saveProfileImage(imageUrl); // Save the new image URL in session manager
    }
}