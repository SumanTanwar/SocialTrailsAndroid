package com.example.socialtrailsapp.adminpanel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.Utils;
import com.example.socialtrailsapp.changePassword;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminChangePasswordActivity extends AdminBottomMenuActivity {

    EditText txtcurrentpwd, txtnewpwd, txtnewconfpwd;
    ImageView eye_currentpwd, eye_newpwd, eye_newconfpwd;
    Button btnchangepwd;
    TextView txtbackprofile;
    FirebaseAuth mAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_admin_change_password);
        getLayoutInflater().inflate(R.layout.activity_admin_change_password, findViewById(R.id.container));


        txtcurrentpwd = findViewById(R.id.txtcurrentpwd);
        txtnewpwd = findViewById(R.id.txtnewpwd);
        txtnewconfpwd = findViewById(R.id.txtnewconfpwd);
        eye_currentpwd = findViewById(R.id.eye_currentpwd);
        eye_newpwd = findViewById(R.id.eye_newpwd);
        eye_newconfpwd = findViewById(R.id.eye_newconfpwd);
        btnchangepwd = findViewById(R.id.btnchangepwd);
        //  txtbackprofile = findViewById(R.id.txtbackprofile);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);

        // region eye image
        eye_currentpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtcurrentpwd, eye_currentpwd);
            }
        });
        eye_newpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtnewpwd, eye_newpwd);
            }
        });
        eye_newconfpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.togglePasswordVisibility(txtnewconfpwd, eye_newconfpwd);
            }
        });


        btnchangepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentpwd = txtcurrentpwd.getText().toString().trim();
                String newpwd = txtnewpwd.getText().toString().trim();
                String confirmpwd = txtnewconfpwd.getText().toString().trim();

                if (TextUtils.isEmpty(currentpwd)) {
                    txtcurrentpwd.setError("Current Password is required");
                    txtcurrentpwd.requestFocus();
                    return;
                } else if (currentpwd.length() < 8) {
                    txtcurrentpwd.setError("Current Password should be more than 8 char");
                    txtcurrentpwd.requestFocus();
                    return;
                } else if (!Utils.isValidPassword(currentpwd)) {
                    txtcurrentpwd.setError("Current Password must contain at least one letter and one digit");
                    txtcurrentpwd.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(newpwd)) {
                    txtnewpwd.setError("New Password is required");
                    txtnewpwd.requestFocus();
                    return;
                } else if (newpwd.length() < 8) {
                    txtnewpwd.setError("New Password should be more than 8 char");
                    txtnewpwd.requestFocus();
                    return;
                } else if (!Utils.isValidPassword(newpwd)) {
                    txtnewpwd.setError("New Password must contain at least one letter and one digit");
                    txtnewpwd.requestFocus();
                    return;
                }
                if (!newpwd.equals(confirmpwd)) {
                    txtnewconfpwd.setError("New Passwords do not match");
                    txtnewconfpwd.requestFocus();
                    return;
                }
                changepassword(currentpwd, newpwd);
            }
        });

    }

    public void changepassword(String currentpwd, String newpwd) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.reauthenticate(EmailAuthProvider.getCredential(currentUser.getEmail(), currentpwd)).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            currentUser.updatePassword(newpwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        sessionManager.logoutUser();
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(AdminChangePasswordActivity.this, "Password successfully changed! Sign in using new password", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AdminChangePasswordActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        txtcurrentpwd.setText("");
                                        txtnewpwd.setText("");
                                        txtnewconfpwd.setText("");

                                        Toast.makeText(AdminChangePasswordActivity.this, "Failed to change password! Please try again after sometime ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            txtcurrentpwd.setText("");
                            txtnewpwd.setText("");
                            txtnewconfpwd.setText("");

                            Toast.makeText(AdminChangePasswordActivity.this, "Authentication failed. Verify your current password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


    }
}
