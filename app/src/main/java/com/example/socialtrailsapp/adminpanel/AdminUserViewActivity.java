package com.example.socialtrailsapp.adminpanel;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.GalleryImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.MainActivity;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.SignInActivity;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;
import com.example.socialtrailsapp.Utility.Utils;
import com.example.socialtrailsapp.userSettingActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdminUserViewActivity extends AdminBottomMenuActivity {

    String userId;
    UserService userService;
    UserPostService userPostService;
    FollowService followService;

    TextView txtprofileusername,txtdetailmail,txtadminbio,profilereason,admindeletetxt,
            btnSuspendProfile,btnDeleteProfile,postscount,followersCount, followingsCount;
    private SessionManager sessionManager;
    List<UserPost> list ;
    ImageView profile_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_user_view, findViewById(R.id.container));
      //  setContentView(R.layout.activity_admin_user_view);
        userId =  getIntent().getStringExtra("intentuserId");
        userService = new UserService();
        userPostService = new UserPostService();
        followService = new FollowService();

        btnSuspendProfile = findViewById(R.id.btnSuspendProfile);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);
        txtprofileusername = findViewById(R.id.txtprofileusername);
        txtdetailmail = findViewById(R.id.txtdetailmail);
        profilereason = findViewById(R.id.profilereason);
        admindeletetxt = findViewById(R.id.admindeletetxt);
        txtadminbio = findViewById(R.id.txtadminbio);
        postscount = findViewById(R.id.adminpostscount);
        followersCount = findViewById(R.id.followers_count);
        followingsCount = findViewById(R.id.followings_count);
        profile_pic = findViewById(R.id.profile_pic);
        sessionManager = SessionManager.getInstance(this);


    //    btnSuspendProfile.setBackgroundResource(R.drawable.button_background);
       // btnSuspendProfile.setBackgroundColor(Color.LTGRAY);
       // btnDeleteProfile.setBackgroundColor(Color.LTGRAY);
        userService.adminGetUserByID(userId, new DataOperationCallback<Users>() {
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
    private void getAllUserPost(String userId)
    {
        userPostService.getAllUserPost(userId, new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> data) {
                list = new ArrayList<>(data);
                Log.d("Post count","count: " + list.size());
                int size = list.size();
                postscount.setText("" + size);
                List<String> imageUrls = new ArrayList<>();
                List<String> postIds = new ArrayList<>();
                for (UserPost post : list) {
                    imageUrls.add(post.getUploadedImageUris().get(0).toString());
                    postIds.add(post.getPostId());
                }

                // Set up the GridView
                GridView gridView = findViewById(R.id.gallery_grid);
                GalleryImageAdapter adapter = new GalleryImageAdapter(AdminUserViewActivity.this, imageUrls,postIds);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {

                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
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
                profilereason.setTextColor(Color.WHITE);
                profilereason.setBackgroundColor(Color.parseColor("#FF9800"));
                profilereason.setText("Suspended profile : " + reason);
                profilereason.setVisibility(View.VISIBLE);
                btnSuspendProfile.setText("UnSuspend Profile");
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
                btnSuspendProfile.setText("Suspended Profile");
                btnSuspendProfile.setOnClickListener(v -> showSuspendDialog());
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(AdminUserViewActivity.this, "activate profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }
private void adminDeleteProfile(String userId)
{
    userService.adminDeleteProfile(userId, new OperationCallback() {
        @Override
        public void onSuccess() {
            Toast.makeText(AdminUserViewActivity.this, "profile deleted successfully done.", Toast.LENGTH_SHORT).show();
            admindeletetxt.setTextColor(Color.WHITE);
            admindeletetxt.setBackgroundColor(Color.RED);
            admindeletetxt.setText("Deleted profile by admin on : " + Utils.getCurrentDatetime());
            admindeletetxt.setVisibility(View.VISIBLE);
            btnDeleteProfile.setText("Activate Profile");
            btnDeleteProfile.setOnClickListener(v -> adminUnDeleteProfile(userId));
        }

        @Override
        public void onFailure(String errMessage) {
            Toast.makeText(AdminUserViewActivity.this, "delete profile failed! Please try again later.", Toast.LENGTH_SHORT).show();
        }
    });
}
private void adminUnDeleteProfile(String userId)
{
    userService.adminUnDeleteProfile(userId, new OperationCallback() {
        @Override
        public void onSuccess() {
            Toast.makeText(AdminUserViewActivity.this, "profile activate successfully done.", Toast.LENGTH_SHORT).show();
            admindeletetxt.setText("");
            admindeletetxt.setVisibility(View.GONE);
            btnDeleteProfile.setText("Delete Profile");
            btnDeleteProfile.setOnClickListener(v -> adminDeleteProfile(userId));
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
        admindeletetxt.setVisibility(View.GONE);
        if (user.getSuspended()) {

            profilereason.setTextColor(Color.WHITE);
            profilereason.setBackgroundColor(Color.parseColor("#FF9800"));
            profilereason.setText("Suspended profile : " + user.getSuspendedreason());
            profilereason.setVisibility(View.VISIBLE);
            btnSuspendProfile.setText("UnSuspend Profile");
            btnSuspendProfile.setOnClickListener(v -> activateProfile(userId));
        } else {
            btnSuspendProfile.setText("Suspend Profile");
            btnSuspendProfile.setOnClickListener(v -> showSuspendDialog());
        }
        if(user.getProfiledeleted())
        {
            admindeletetxt.setTextColor(Color.WHITE);
            admindeletetxt.setBackgroundColor(Color.RED);
            admindeletetxt.setText("user deleted own profile");
            admindeletetxt.setVisibility(View.VISIBLE);
            btnSuspendProfile.setVisibility(View.GONE);
            btnDeleteProfile.setVisibility(View.GONE);
        }
        else if (user.getAdmindeleted()) {
            admindeletetxt.setTextColor(Color.WHITE);
            admindeletetxt.setBackgroundColor(Color.RED);
            admindeletetxt.setText("Deleted profile by admin on : " + user.getAdmindeletedon());
            admindeletetxt.setVisibility(View.VISIBLE);
            btnDeleteProfile.setText("Activate Profile");
            btnDeleteProfile.setOnClickListener(v -> adminUnDeleteProfile(userId));
        } else {
            btnDeleteProfile.setText("Delete Profile");
            btnDeleteProfile.setOnClickListener(v -> adminDeleteProfile(userId));
        }
        if (user.getProfilepicture() != null &&  !user.getProfilepicture().isEmpty()) {
            Uri profileImageUri = Uri.parse(user.getProfilepicture()); // Convert String to Uri
            Glide.with(this)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(profile_pic);
        } else {
            Glide.with(this)
                    .load(R.drawable.user) // Replace with your image URI or resource
                    .transform(new CircleCrop())
                    .into(profile_pic);
        }
        getAllUserPost(user.getUserId());
        getFollowersCount(user.getUserId());
        getFollowingCount(user.getUserId());
    }

    private void getFollowersCount(String userId) {
        followService.getFollowersCount(userId, new DataOperationCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                followersCount.setText(String.valueOf(count));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error fetching followers count: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFollowingCount(String userId) {
        followService.getFollowingCount(userId, new DataOperationCallback<Integer>() {
            @Override
            public void onSuccess(Integer count) {
                followingsCount.setText(String.valueOf(count));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error fetching following count: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

}