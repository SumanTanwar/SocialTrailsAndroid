package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.adminpanel.DashBoardActivity;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int splashtimeout = 3000;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = SessionManager.getInstance(this);

        if (sessionManager.userLoggedIn()) {
            Log.d("admin", "onCreate: " + sessionManager.getroleType());
            Log.d("admin1", "onCreate: " + UserRole.ADMIN.getRole());
            if(sessionManager.getroleType().equals(UserRole.ADMIN.getRole()))
            {
                Intent intent = new Intent(SplashScreenActivity.this, DashBoardActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        } else {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, splashtimeout);
        }
    }
}