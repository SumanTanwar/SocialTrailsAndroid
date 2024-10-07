package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

        Button buttonListModerators = findViewById(R.id.buttonListModerators);
        buttonListModerators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ModeratorlistActivity.class);
                startActivity(intent);
            }
        });


    }
}