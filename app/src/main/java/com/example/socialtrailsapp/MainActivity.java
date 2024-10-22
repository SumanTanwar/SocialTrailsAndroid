package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.Utility.SessionManager;

public class MainActivity extends BottomMenuActivity {
    private SessionManager sessionManager;
    TextView txtmains;
    Button btnNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.container));

        txtmains = findViewById(R.id.txtdata);
        sessionManager = SessionManager.getInstance(this);
        btnNotify = findViewById(R.id.btnNotify);

        txtmains.setText("Name: " + sessionManager.getUsername() + "\n" +
                "Email: " + sessionManager.getEmail() + "\n" +
                "Role: " + sessionManager.getroleType());
        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FollowNotificationActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}