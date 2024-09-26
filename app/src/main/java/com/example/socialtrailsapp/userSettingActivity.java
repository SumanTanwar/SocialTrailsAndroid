package com.example.socialtrailsapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class userSettingActivity extends BottomMenuActivity {

    TextView txtLogout, txtprofileuser, txtchangepwd, txtdelprofile;
    private SessionManager sessionManager;
    UserService userService;
    FirebaseAuth mAuth;
    Switch switchNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_user_setting, findViewById(R.id.container));

        txtLogout = findViewById(R.id.txtLogout);
        txtprofileuser = findViewById(R.id.txtprofileusername);
        txtchangepwd = findViewById(R.id.txtchangepwd);
        txtdelprofile = findViewById(R.id.txtdelprofile);
        switchNotify = findViewById(R.id.switchNotify);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();

        if (sessionManager.userLoggedIn()) {
            txtprofileuser.setText(sessionManager.getUsername());
            switchNotify.setChecked(sessionManager.getNotificationStatus());
        }

        txtchangepwd.setOnClickListener(view -> {
            Intent intent = new Intent(userSettingActivity.this, changePassword.class);
            startActivity(intent);
            finish();
        });

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
}
