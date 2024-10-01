package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialtrailsapp.Utility.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends BottomMenuActivity {

    EditText nameEdit, emailEdit, bioEdit;
    TextView backText;
    Button saveBtn;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_profile, findViewById(R.id.container));

        nameEdit = findViewById(R.id.nameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        bioEdit = findViewById(R.id.bioEdit);
        backText = findViewById(R.id.backText);
        saveBtn = findViewById(R.id.saveBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String bio = intent.getStringExtra("bio");

        nameEdit.setText(name);
        emailEdit.setText(email);
        bioEdit.setText(bio);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    updateProfile(currentUser);
                }
            }
        });

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, userSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateProfile(FirebaseUser user) {
        String newName = nameEdit.getText().toString().trim();
        String newEmail = emailEdit.getText().toString().trim();
        String newBio = bioEdit.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!user.getEmail().equals(newEmail)) {
                            user.updateEmail(newEmail).addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    updateUserDataInDatabase(newName, newEmail, newBio);
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Email update failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            updateUserDataInDatabase(newName, newEmail, newBio);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserDataInDatabase(String name, String email, String bio) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).child("username").setValue(name);
            mDatabase.child("users").child(userId).child("email").setValue(email);
            mDatabase.child("users").child(userId).child("bio").setValue(bio);

            SessionManager sessionManager = SessionManager.getInstance(this);
            sessionManager.updateUserInfo(name, email);

            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("bio", bio);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
