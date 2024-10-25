package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.R;

public class DashBoardActivity extends AdminBottomMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.admin_activity_dashboard);
        getLayoutInflater().inflate(R.layout.admin_activity_dashboard, findViewById(R.id.container));

        LinearLayout Userlistsection = findViewById(R.id.Userlistsection);
        Userlistsection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, AdminListofUsersActivity.class);
                startActivity(intent);
            }
        });


    }
}