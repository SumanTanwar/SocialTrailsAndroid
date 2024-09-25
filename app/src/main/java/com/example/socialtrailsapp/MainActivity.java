package com.example.socialtrailsapp;

import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.container));

        txtmains = findViewById(R.id.txtdata);
        sessionManager = SessionManager.getInstance(this);

        txtmains.setText("name " + sessionManager.getUsername() + " email : " + sessionManager.getEmail() + " role : " + sessionManager.getroleType());
    }
}