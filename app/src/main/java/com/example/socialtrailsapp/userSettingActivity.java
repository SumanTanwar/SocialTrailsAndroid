package com.example.socialtrailsapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class userSettingActivity extends BottomMenuActivity {

    TextView txtLogout, txtprofileuser, txtchangepwd, txtdelprofile;
    private SessionManager sessionManager;
    ImageView profile;
    UserService userService;
    FirebaseAuth mAuth;
    Switch switchNotify;

    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_user_setting, findViewById(R.id.container));

        txtLogout = findViewById(R.id.txtLogout);
      txtprofileuser = findViewById(R.id.txtprofileusername);
        txtchangepwd = findViewById(R.id.txtchangepwd);
       // txtEditProfile = findViewById(R.id.txtEditProfile);
        profile = findViewById(R.id.profileImageView);
        txtdelprofile = findViewById(R.id.txtdelprofile);
        switchNotify = findViewById(R.id.switchNotify);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();

        if (sessionManager.userLoggedIn()) {
            txtprofileuser.setText(sessionManager.getUsername());
            switchNotify.setChecked(sessionManager.getNotificationStatus());
            String profileImageUriString = sessionManager.getProfileImage();
            if (sessionManager.getProfileImage() != null) {
                Uri profileImageUri = Uri.parse(sessionManager.getProfileImage()); // Convert String to Uri
                Glide.with(this)
                        .load(profileImageUri)
                        .transform(new CircleCrop())
                        .into(profile);
            } else {
                Glide.with(this)
                        .load(R.drawable.user) // Replace with your image URI or resource
                        .transform(new CircleCrop())
                        .into(profile);
            }
        }

        txtchangepwd.setOnClickListener(view -> {
            Intent intent = new Intent(userSettingActivity.this, changePassword.class);
            startActivity(intent);
        });

//        txtEditProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(userSettingActivity.this, EditProfileActivity.class);
//                intent.putExtra("name", sessionManager.getUsername());
//                intent.putExtra("email",mAuth.getCurrentUser().getEmail());
//                intent.putExtra("bio", sessionManager.getBio());
//                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
//            }
//        });


        txtLogout.setOnClickListener(view -> {
            sessionManager.logoutUser();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(userSettingActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        txtdelprofile.setOnClickListener(view -> {
            new AlertDialog.Builder(userSettingActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            userService.deleteProfile(sessionManager.getUserID(), new OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    user.delete().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Utils.saveCredentials(userSettingActivity.this, "", false);
                                            sessionManager.logoutUser();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(userSettingActivity.this, SignInActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            userService.setbackdeleteProfile(sessionManager.getUserID());
                                            Toast.makeText(userSettingActivity.this, "Delete profile failed, please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(String errMessage) {
                                    userService.setbackdeleteProfile(sessionManager.getUserID());
                                    Toast.makeText(userSettingActivity.this, "Delete profile failed, please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(userSettingActivity.this, "Delete profile failed, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        switchNotify.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            userService.setNotification(sessionManager.getUserID(), isChecked, new OperationCallback() {
                @Override
                public void onSuccess() {
                    sessionManager.setNotificationStatus(isChecked);
                    Toast.makeText(userSettingActivity.this,
                            isChecked ? "Notifications turned ON" : "Notifications have been turned OFF. You can enable them again in the settings.",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errMessage) {
                    switchNotify.setChecked(sessionManager.getNotificationStatus());
                    Toast.makeText(userSettingActivity.this, "Notification setting failed! Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {
            // Check if data is not null
            if (data != null) {
                String newName = data.getStringExtra("name");
                if (newName != null) {
                    txtprofileuser.setText(newName);
                }
            }
        }
    }

}
