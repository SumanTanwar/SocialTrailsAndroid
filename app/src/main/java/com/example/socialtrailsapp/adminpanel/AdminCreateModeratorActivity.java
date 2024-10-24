package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminCreateModeratorActivity extends AdminBottomMenuActivity {

    private EditText nameEditText, emailEditText;
    private Button createUserButton;
    private FirebaseAuth mAuth;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_create_moderator, findViewById(R.id.container));

        mAuth = FirebaseAuth.getInstance();
        userService = new UserService();

        nameEditText = findViewById(R.id.Musername);
        emailEditText = findViewById(R.id.Memail);
        createUserButton = findViewById(R.id.btnCreateUser);

        createUserButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (validateInputs(name, email)) {
                createModerator(name, email);
            }
        });
    }

    private boolean validateInputs(String name, String email) {
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return false;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        return true;
    }

    // Create a new moderator and send password reset email
    private void createModerator(String name, String email) {
        // Use a default temporary password
        String temporaryPassword = "tempass123";

        mAuth.createUserWithEmailAndPassword(email, temporaryPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            // Create user record in Firestore or Realtime Database
                            String uid = currentUser.getUid();
                            Users user = new Users(uid, name, email, UserRole.MODERATOR.getRole());
                            userService.createUser(user, new OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    // Send password reset email
                                    sendPasswordResetEmail(email);
                                    clearInputs();

                                    // Sign out after moderator creation
                                    mAuth.signOut();

                                    // Navigate to the moderator list
                                    Intent intent = new Intent(AdminCreateModeratorActivity.this, ModeratorlistActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure(String errMessage) {
                                    Toast.makeText(AdminCreateModeratorActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(AdminCreateModeratorActivity.this, "Error creating moderator! Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = (task.getException() != null) ? task.getException().getMessage() : "Create moderator failed! Please try again later.";
                        Toast.makeText(AdminCreateModeratorActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminCreateModeratorActivity.this, "Password reset email sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminCreateModeratorActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearInputs() {
        nameEditText.setText("");
        emailEditText.setText("");
    }
}