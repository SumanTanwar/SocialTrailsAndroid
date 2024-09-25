package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;

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

    }
}