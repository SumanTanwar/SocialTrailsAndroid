package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_menu);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(R.id.navigation_dashboard == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, MainActivity.class));
                    return true;
                }
                else if(R.id.navigation_post == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, CreatePostActivity.class));
                    return true;
                }
                else if(R.id.navigation_profile == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, ViewProfileActivity.class));
                    return true;
                }
                else if(R.id.navigation_setting == item.getItemId())
                {
                    startActivity(new Intent(BottomMenuActivity.this, userSettingActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
    }
