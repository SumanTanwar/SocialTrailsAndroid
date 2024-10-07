package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

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
                String generatedPassword = generateRandomPassword(8); // Generate a random password
                Log.d("AdminCreateModerator", "Generated Password: " + generatedPassword); // Log the generated password
                createModerator(name, email, generatedPassword); // Call method to create moderator
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

    // Generate a random password
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }


    private void createModerator(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    currentUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            String uid = currentUser.getUid();
                            Users user = new Users(uid, name, email, UserRole.MODERATOR.getRole());
                            userService.createUser(user, new OperationCallback() {
                                @Override
                                public void onSuccess() {
                                    sendGeneratedPasswordEmail(email, password); // Send the generated password via email
                                    clearInputs();


                                    mAuth.signOut();


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
                            Toast.makeText(AdminCreateModeratorActivity.this, "Failed to send verification email to moderator.", Toast.LENGTH_SHORT).show();
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


    private void sendGeneratedPasswordEmail(String email, String password) {
        Log.d("EmailDebug", "Sending email to: " + email + " with password: " + password);

        String subject = "Your Moderator Account Creation";
        String message = "Hello,\n\nYour account has been created successfully.\n\n"
                + "Here are your login details:\n"
                + "Email: " + email + "\n"
                + "Password: " + password + "\n\n"
                + "Please change your password after your first login.\n\n"
                + "Thank you!";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AdminCreateModeratorActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        nameEditText.setText("");
        emailEditText.setText("");
    }
}
