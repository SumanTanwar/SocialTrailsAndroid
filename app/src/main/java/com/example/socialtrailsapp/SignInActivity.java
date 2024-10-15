package com.example.socialtrailsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.example.socialtrailsapp.adminpanel.DashBoardActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;


    EditText txtloginusername,txtloginpwd;
    Button btnsignin;
    TextView btnregister,resetPassword;
    ImageView eye_loginpwd;
    CheckBox chkrememberMe;
    FirebaseAuth mAuth ;
    UserService userService;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtloginusername = findViewById(R.id.txtloginusername);
        txtloginpwd = findViewById(R.id.txtloginpwd);
        btnsignin = findViewById(R.id.btnsignin);
        resetPassword = findViewById(R.id.btnforgotpwd);
        btnregister = findViewById(R.id.btnregister);
        eye_loginpwd = findViewById(R.id.eye_loginpwd);
        chkrememberMe = findViewById(R.id.chkrememberMe);
        mAuth = FirebaseAuth.getInstance();
        userService = new UserService();
        sessionManager = SessionManager.getInstance(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String savedEmail = preferences.getString("email", "");
        boolean rememberMeChecked = preferences.getBoolean("rememberMe", false);

        txtloginusername.setText(savedEmail);
        chkrememberMe.setChecked(rememberMeChecked);
        //region signup and signin button click
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = txtloginpwd.getText().toString();
                if (TextUtils.isEmpty(txtloginusername.getText().toString().trim())) {
                    txtloginusername.setError("Email address is required");
                    txtloginusername.requestFocus();
                    return ;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(txtloginusername.getText().toString()).matches()){
                    txtloginusername.setError("Invalid email address");
                    txtloginusername.requestFocus();
                    return ;
                }

                if (TextUtils.isEmpty(password)) {
                    txtloginpwd.setError("Password is required");
                    txtloginpwd.requestFocus();
                    return ;
                }
                else if(password.length() < 8)
                {
                    txtloginpwd.setError("Password should be more than 8 char");
                    txtloginpwd.requestFocus();
                    return;
                }
                else if (!Utils.isValidPassword(password)) {
                    txtloginpwd.setError("Password must contain at least one letter and one digit");
                    txtloginpwd.requestFocus();
                    return;
                }
                if (txtloginusername.getText().toString().trim().equalsIgnoreCase("socialtrails2024@gmail.com")) {
                    validateAdmin();
                }
                else {
                    validateUser();
                }
            }
        });
        eye_loginpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtloginpwd, eye_loginpwd);
            }
        });

        //endregion
    }
    //region validate user in database
    private void validateAdmin()
    {
        String usernameEmailLogin = txtloginusername.getText().toString().trim();
        String password = txtloginpwd.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(usernameEmailLogin,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    if (chkrememberMe.isChecked()) {
                        Utils.saveCredentials(SignInActivity.this, usernameEmailLogin, true);
                    } else {
                        Utils.saveCredentials(SignInActivity.this, "", false);
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        sessionManager.logoutUser();
                        sessionManager.loginUser(user.getUid(), "Admin", user.getEmail(), true, UserRole.ADMIN.getRole(),"bio","");
                        Intent intent = new Intent(SignInActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();  // Optionally sign out the user
                    }
                }

                else
                {
                    txtloginpwd.setText("");
                    Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            }
        });
    }

    private void validateUser()
    {
        String usernameEmailLogin = txtloginusername.getText().toString().trim();
        String password = txtloginpwd.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(usernameEmailLogin,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {

                    if (chkrememberMe.isChecked()) {
                        Utils.saveCredentials(SignInActivity.this, usernameEmailLogin, true);
                    } else {
                        Utils.saveCredentials(SignInActivity.this,"", false);
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {


                        if (user.isEmailVerified()) {


                            userService.getUserByID(user.getUid(), new DataOperationCallback<Users>() {

                                @Override
                                public void onSuccess(Users data) {
                                    if(data.getSuspended() == true)
                                    {
                                        Toast.makeText(SignInActivity.this, "Your account has been suspended by admin .please contact social trails teams.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                    else {
                                        sessionManager.logoutUser();
                                        sessionManager.loginUser(data.getUserId(), data.getUsername(), data.getEmail(), data.getNotification(), data.getRoles(),data.getBio(),data.getProfilepicture());
                                        if(data.getRoles().equals(UserRole.MODERATOR.getRole()))
                                        {
                                            Toast.makeText(SignInActivity.this, "Moderators is coming soon.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                        }
                                        else {
                                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(String errMessage) {
                                    txtloginpwd.setText("");
                                    Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            });

                        } else {
                            // Email not verified
                            Toast.makeText(SignInActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();  // Optionally sign out the user
                        }
                    }
                    else
                    {
                        txtloginpwd.setText("");
                        Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
                else
                {
                    txtloginpwd.setText("");
                    Toast.makeText(SignInActivity.this, "Invalid email address and password", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }
            }
        });
    }




    //endregion

}