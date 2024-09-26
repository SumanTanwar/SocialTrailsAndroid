package com.example.socialtrailsapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class userSettingActivity extends BottomMenuActivity {

    TextView txtLogout,txtprofileuser,txtchangepwd,txtdelprofile;
    private SessionManager sessionManager;
    UserService userService;
    FirebaseAuth mAuth;
    Switch switchNotify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_user_setting);
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

        txtchangepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userSettingActivity.this, changePassword.class);
                startActivity(intent);
                finish();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logoutUser();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(userSettingActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


        txtdelprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(userSettingActivity.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account")
                        .setPositiveButton("Yes",(dialog,which) -> {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null)
                            {
                                userService.deleteProfile(sessionManager.getUserID(), new OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful())
                                                {
                                                    Utils.saveCredentials(userSettingActivity.this,"",false);
                                                    sessionManager.logoutUser();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(userSettingActivity.this,SignInActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else
                                                {
                                                    userService.setbackdeleteProfile(sessionManager.getUserID());
                                                    Toast.makeText(userSettingActivity.this,
                                                            "Delete profile failed, Pleasetry again later",
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(String errMessage) {

                                        userService.setbackdeleteProfile(sessionManager.getUserID());
                                        Toast.makeText(userSettingActivity.this,
                                                "Delete profile failed, Pleasetry again later",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(userSettingActivity.this,
                                        "Delete profile failed, Pleasetry again later",
                                        Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("No",(dilog,which) -> dilog.dismiss())
                        .show();
            }
        });



//        txtdelprofile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                FirebaseUser user = mAuth.getCurrentUser();
//                if(user !=  null) {
//                    userService.deleteProfile(sessionManager.getUserID(), new OperationCallback() {
//                        @Override
//                        public void onSuccess() {
//                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Utils.saveCredentials(userSettingActivity.this,"", false);
//                                        sessionManager.logoutUser();
//                                        FirebaseAuth.getInstance().signOut();
//                                        Intent intent = new Intent(userSettingActivity.this, SignInActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    } else {
//                                        userService.setbackdeleteProfile(sessionManager.getUserID());
//                                        Toast.makeText(userSettingActivity.this, "Delete Profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onFailure(String errMessage) {
//                            userService.setbackdeleteProfile(sessionManager.getUserID());
//                            Toast.makeText(userSettingActivity.this, "Delete Profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                else
//                {
//                    Toast.makeText(userSettingActivity.this, "Delete Profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }
}