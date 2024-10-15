package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.changePassword;
import com.example.socialtrailsapp.userSettingActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminSettingActivity extends AdminBottomMenuActivity {
    TextView txtLogout, txtChangePassword, txtcreatemoderator;
    private SessionManager sessionManager;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
    //   setContentView(R.layout.activity_admin_setting);
        getLayoutInflater().inflate(R.layout.activity_admin_setting, findViewById(R.id.container));

        txtLogout = findViewById(R.id.txtLogout);
        txtChangePassword = findViewById(R.id.txtchangepwd);
        txtcreatemoderator = findViewById(R.id.txtcreatemoderator);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);

        txtcreatemoderator.setOnClickListener(view -> {
            // Start the Change Password Activity
            Intent intent = new Intent(AdminSettingActivity.this, AdminCreateModeratorActivity.class);
            startActivity(intent);
        });

        txtChangePassword.setOnClickListener(view -> {
            // Start the Change Password Activity
            Intent intent = new Intent(AdminSettingActivity.this, AdminChangePasswordActivity.class);
            startActivity(intent);
        });

        txtLogout.setOnClickListener(view -> {
            sessionManager.logoutUser();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminSettingActivity.this, SignInActivity.class);
            startActivity(intent);
            finish(); // Close the activity
        });


    }
}