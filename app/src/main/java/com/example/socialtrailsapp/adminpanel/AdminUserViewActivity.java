package com.example.socialtrailsapp.adminpanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.MainActivity;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.userSettingActivity;

import org.w3c.dom.Text;

public class AdminUserViewActivity extends AdminBottomMenuActivity {
    Button btnSuspendProfile;
    String userId;
    UserService userService;
    TextView txtprofileusername,txtdetailmail,txtadminbio,profilereason;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_user_view, findViewById(R.id.container));
      //  setContentView(R.layout.activity_admin_user_view);
        userId =  getIntent().getStringExtra("intentuserId");
        userService = new UserService();
        btnSuspendProfile = findViewById(R.id.btnSuspendProfile);
        txtprofileusername = findViewById(R.id.txtprofileusername);
        txtdetailmail = findViewById(R.id.txtdetailmail);
        profilereason = findViewById(R.id.profilereason);
        txtadminbio = findViewById(R.id.txtadminbio);
        sessionManager = SessionManager.getInstance(this);

        userService.getUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                setDetail(data);
            }

            @Override
            public void onFailure(String errMessage) {
                Intent intent = new Intent(AdminUserViewActivity.this, AdminListofUsersActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(AdminUserViewActivity.this, "something wrong ! please try again later.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void showSuspendDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_suspend_profile, null);
        final EditText reasonInput = dialogView.findViewById(R.id.reasonInput);
        final TextView txtreasontitle = dialogView.findViewById(R.id.txtreasontitle);
        txtreasontitle.setText("Please provide a reason for suspending " + txtprofileusername.getText().toString() + "'s profile:");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suspend Profile")
                .setView(dialogView)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            confirmButton.setOnClickListener(v -> {
                String reason = reasonInput.getText().toString();
                if (TextUtils.isEmpty(reason)) {
                    reasonInput.setError("Suspend reason is required");
                    reasonInput.requestFocus();
                } else {
                    suspendprofile(userId, reason);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
    private void suspendprofile(String userId,String reason)
    {

        userService.suspendProfile(userId, sessionManager.getUserID(),reason, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminUserViewActivity.this, "suspend profile successfully done.", Toast.LENGTH_SHORT).show();
                profilereason.setText("suspended profile : " + reason);
                profilereason.setVisibility(View.VISIBLE);
                btnSuspendProfile.setText("Activate Profile");
                btnSuspendProfile.setOnClickListener(v -> activateProfile(userId));
            }

            @Override
            public void onFailure(String errMessage) {
                profilereason.setVisibility(View.GONE);
                Toast.makeText(AdminUserViewActivity.this, "suspend profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void activateProfile(String userId)
    {
        userService.activateProfile(userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminUserViewActivity.this, "activate profile successfully done.", Toast.LENGTH_SHORT).show();
                profilereason.setVisibility(View.GONE);
                profilereason.setText("");
                btnSuspendProfile.setText("Suspend Profile");
                btnSuspendProfile.setOnClickListener(v -> showSuspendDialog());
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(AdminUserViewActivity.this, "activate profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setDetail(Users user)
    {
        txtprofileusername.setText(user.getUsername());
        txtdetailmail.setText(user.getEmail());
        txtadminbio.setText(user.getBio());
        profilereason.setVisibility(View.GONE);
        if (user.getSuspended()) {
            profilereason.setText("suspended profile : " + user.getSuspendedreason());
            profilereason.setVisibility(View.VISIBLE);
            btnSuspendProfile.setText("Activate Profile");
            btnSuspendProfile.setOnClickListener(v -> activateProfile(userId));
        } else {
            btnSuspendProfile.setText("Suspend Profile");
            btnSuspendProfile.setOnClickListener(v -> showSuspendDialog());
        }
    }
}