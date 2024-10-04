package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;

public class ViewProfileActivity extends BottomMenuActivity {

    Button btnEdit;
    TextView txtprofileuser,bio;
    SessionManager sessionManager;
    UserService userService;
    FirebaseAuth mAuth;

    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_view_profile, findViewById(R.id.container));

        btnEdit = findViewById(R.id.btnEditProfile);
        txtprofileuser = findViewById(R.id.txtprofileusername);
        bio = findViewById(R.id.bio);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);
        userService = new UserService();

        if (sessionManager.userLoggedIn())
        {
            txtprofileuser.setText(sessionManager.getUsername());
            bio.setText(sessionManager.getBio());
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("name", sessionManager.getUsername());
                intent.putExtra("email",mAuth.getCurrentUser().getEmail());
                intent.putExtra("bio", sessionManager.getBio());
                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK) {

            if (data != null) {
                String newName = data.getStringExtra("name");
                if (newName != null) {
                    txtprofileuser.setText(newName);
                }
                String newBio = data.getStringExtra("bio");
                if (newBio != null) {
                    bio.setText(newBio);
                    sessionManager.updateUserInfo(newName, newBio);
                }
            }
        }
    }



    public void showDataUser() {
        Intent intent = getIntent();
        txtprofileuser.setText(intent.getStringExtra("name"));

    }
}