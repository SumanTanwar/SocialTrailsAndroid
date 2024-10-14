package com.example.socialtrailsapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.EditImagePagerAdapter;
import com.example.socialtrailsapp.CustomAdapter.ImagePagerAdapter;
import com.example.socialtrailsapp.CustomAdapter.PostImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.ILocationSetter;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.MapDialog;
import com.example.socialtrailsapp.Utility.PostImagesService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class UserPostEditActivity extends BottomMenuActivity implements EditImagePagerAdapter.OnImageRemovedListener, ILocationSetter {
    private UserPostService userPostService;
    private PostImagesService postImagesService;
    private String postdetailId;
    ImageView userProfileImage;
    TextView userName,userLocation,postCaption, btncancel,btneditpost;
    RecyclerView imagesRecyclerView;
    private SessionManager sessionManager;
    private EditImagePagerAdapter postImageAdapter;
    private double latitude,longitude;
    private String tagLocation;
     private Boolean isEditLocation  = false;
    private boolean isUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_post_edit);
        getLayoutInflater().inflate(R.layout.activity_user_post_edit, findViewById(R.id.container));
        userPostService = new UserPostService();
        userProfileImage = findViewById(R.id.edituserProfileImage);
        userName = findViewById(R.id.edituserName);
        userLocation = findViewById(R.id.edituserLocation);
        postCaption = findViewById(R.id.editpostCaption);
        btncancel = findViewById(R.id.btncancel);
        btneditpost = findViewById(R.id.btneditpost);
        sessionManager = SessionManager.getInstance(this);
        postImagesService = new PostImagesService();
        Intent intent = getIntent();
        postdetailId = intent.getStringExtra("postdetailId");
        imagesRecyclerView = findViewById(R.id.editimagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        checkPermissions();
        setupTextWatchers();
        userPostService.getPostByPostId(postdetailId, new DataOperationCallback<UserPost>() {

            @Override
            public void onSuccess(UserPost data) {
                setPostDetail(data);
            }

            @Override
            public void onFailure(String errMessage) {

                Toast.makeText(UserPostEditActivity.this, "Post edit failed! Please try again later.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserPostEditActivity.this, UserPostDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPostEditActivity.this, UserPostDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng existingLatLng = new LatLng(latitude, longitude);
                MapDialog mapDialog = new MapDialog(UserPostEditActivity.this, existingLatLng, tagLocation);
                mapDialog.show();
            }
        });

       btneditpost.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String caption = postCaption.getText().toString().trim();
               if (TextUtils.isEmpty(caption)) {
                   postCaption.setError("Caption is required");
                   postCaption.requestFocus();
                   return ;
               }
               if(tagLocation == null || tagLocation.isEmpty())
               {
                   Toast.makeText(getApplicationContext(), "Please tag the location", Toast.LENGTH_SHORT).show();
                   return;
               }

              if(isEditLocation || isUpdated) {
                  UserPost userPost = new UserPost(postdetailId, caption, tagLocation, latitude, longitude);
                  userPostService.updateUserPost(userPost, new OperationCallback() {
                      @Override
                      public void onSuccess() {
                          Intent intent = new Intent(UserPostEditActivity.this, UserPostDetailActivity.class);
                          startActivity(intent);
                          finish();
                      }

                      @Override
                      public void onFailure(String errMessage) {
                          Toast.makeText(UserPostEditActivity.this, "edit post failed! Please try again later.", Toast.LENGTH_SHORT).show();
                      }
                  });
              }
              else
              {
                  Intent intent = new Intent(UserPostEditActivity.this, UserPostDetailActivity.class);
                  startActivity(intent);
                  finish();
              }
           }
       });

    }
    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isUpdated = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        };

        postCaption.addTextChangedListener(textWatcher);

    }
    private void setPostDetail(UserPost post)
    {
        if (sessionManager.getProfileImage() != null) {
            Uri profileImageUri = Uri.parse(sessionManager.getProfileImage());
            Glide.with(this)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(userProfileImage);

        } else {
            Glide.with(this)
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(userProfileImage);

        }

        userName.setText(sessionManager.getUsername());

        userLocation.setText(post.getLocation());
        postCaption.setText(post.getCaptiontext());
        List<Uri> imageUris = post.getUploadedImageUris();
        postImageAdapter = new EditImagePagerAdapter((ArrayList<Uri>) imageUris,this,this);
        imagesRecyclerView.setAdapter(postImageAdapter);
        latitude = post.getLatitude();
        longitude = post.getLongitude();
        tagLocation = post.getLocation();
    }


    @Override
    public void onImageRemoved(String imageUrl) {
        postImagesService.deleteImage(postdetailId, imageUrl, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserPostEditActivity.this, "Post Image successfully removed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(UserPostEditActivity.this, "Post image remove failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setLocation(LatLng latLng, String address) {

            longitude = latLng.longitude;
            latitude = latLng.latitude;
            tagLocation = address;
            userLocation.setText(tagLocation);
            isEditLocation = true;

    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Glide.with(this).clear(userProfileImage);
//    }
}