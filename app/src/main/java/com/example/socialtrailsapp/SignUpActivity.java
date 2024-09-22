package com.example.socialtrailsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    EditText txtpwd,txtconfpwd,txtusername,txtemail;
    ImageView eye_password,eye_confpassword;
    Button btnsignup;
    TextView btnlogin;
    CheckBox chkterms;
    FirebaseAuth mAuth;
    UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtusername = findViewById(R.id.txtusername);

        txtemail = findViewById(R.id.txtemail);
        btnsignup = findViewById(R.id.btnsignup);
        btnlogin = findViewById(R.id.btnlogin);
        txtpwd = findViewById(R.id.txtpwd);
        txtconfpwd = findViewById(R.id.txtconfpwd);
        eye_password = findViewById(R.id.eye_password);
        eye_confpassword = findViewById(R.id.eye_confpassword);
        chkterms = findViewById(R.id.chkterms);

        userService = new UserService();
        mAuth = FirebaseAuth.getInstance();
        //region button on click
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // endregion


        //region icon password click
        eye_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtpwd, eye_password);
            }
        });

        eye_confpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtconfpwd, eye_confpassword);
            }
        });
        //endregion
    }

    //region user function

    //validate user
    public void registerUser() {
        String username = txtusername.getText().toString().trim();
        String email = txtemail.getText().toString().trim();
        String password = txtpwd.getText().toString();
        String confirmPassword = txtconfpwd.getText().toString();

        // Input validation
        if (TextUtils.isEmpty(username)) {
            txtusername.setError("User name is required");
            txtusername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            txtemail.setError("Email is required");
            txtemail.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtemail.setError("Invalid email address");
            txtemail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            txtpwd.setError("Password is required");
            txtpwd.requestFocus();
            return;
        } else if (password.length() < 8) {
            txtpwd.setError("Password should be more than 8 characters");
            txtpwd.requestFocus();
            return;
        } else if (!Utils.isValidPassword(password)) {
            txtpwd.setError("Password must contain at least one letter and one digit");
            txtpwd.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            txtconfpwd.setError("Passwords do not match");
            txtconfpwd.requestFocus();
            return;
        }
        if (!chkterms.isChecked()) {
            Toast.makeText(SignUpActivity.this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        // Send email verification
                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    String uid = currentUser.getUid();
                                    Users user = new Users(uid, username, email, UserRole.USER.getRole());
                                    userService.createUser(user, new OperationCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(SignUpActivity.this, "User registered successfully. Please verify your email.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(String errMessage) {
                                            Toast.makeText(SignUpActivity.this, errMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign up failed! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = (task.getException() != null) ? task.getException().getMessage() : "Sign up failed! Please try again later.";
                    Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //endregion

}