package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminSettingActivity extends AdminBottomMenuActivity {
    TextView txtLogout, txtChangePassword, txtcreatemoderator, txtAdmin, txtHeader;
    private SessionManager sessionManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_setting, findViewById(R.id.container));

        txtLogout = findViewById(R.id.txtLogout);
        txtChangePassword = findViewById(R.id.txtchangepwd);
        txtcreatemoderator = findViewById(R.id.txtcreatemoderator);
        txtHeader = findViewById(R.id.txtHeader);
        txtAdmin = findViewById(R.id.txtadmin);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);

        
        String userId = sessionManager.getUserID();
        txtHeader.setText("Admin Settings");
        txtAdmin.setText("Admin");
        txtcreatemoderator.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            String uid = currentUser.getUid();


            UserService userService = new UserService();
            userService.getUserByID(userId, new DataOperationCallback<Users>() {
                @Override
                public void onSuccess(Users data) {
                    String role = data.getRoles();

                    if (role.equals(UserRole.MODERATOR.getRole())) {
                        // If the user is a moderator, update the UI
                        txtAdmin.setText("Moderator");
                        txtHeader.setText("Moderator Settings");
                        txtcreatemoderator.setVisibility(View.GONE); // Hide for moderator
                    }
                }

                @Override
                public void onFailure(String errMessage) {
                //    Toast.makeText(AdminSettingActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }


        txtcreatemoderator.setOnClickListener(view -> {
            Intent intent = new Intent(AdminSettingActivity.this, AdminCreateModeratorActivity.class);
            startActivity(intent);
        });

        txtChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(AdminSettingActivity.this, AdminChangePasswordActivity.class);
            startActivity(intent);
        });

        txtLogout.setOnClickListener(view -> {
            sessionManager.logoutUser();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminSettingActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
