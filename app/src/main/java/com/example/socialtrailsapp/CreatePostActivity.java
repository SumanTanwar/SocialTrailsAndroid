package com.example.socialtrailsapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.ImagePagerAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.ILocationSetter;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.MapDialog;
import com.example.socialtrailsapp.Utility.NotificationService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.adminpanel.DashBoardActivity;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends BottomMenuActivity  implements ImagePagerAdapter.OnImageRemovedListener, ILocationSetter {
    private SessionManager sessionManager;
    private static final int PICK_IMAGE = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private ImagePagerAdapter adapter;
    private RecyclerView recyclerView;

    ImageView profileImage,icon_add_location,btnpost,imgCapture;
    TextView txtpostuserName,txtselectedAddress;
    EditText txtpostcaption;
    private LinearLayout myLinearLayout;
    UserPostService userPostService;
    FollowService followService;
    NotificationService notificationService;
    private double latitude,longitude;
    private String tagLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_create_post);
        getLayoutInflater().inflate(R.layout.activity_create_post, findViewById(R.id.container));
        recyclerView = findViewById(R.id.recyclerView);
        btnpost = findViewById(R.id.btnpost);
        profileImage = findViewById(R.id.profileImage);
        txtpostuserName = findViewById(R.id.txtpostuserName);
        txtpostcaption = findViewById(R.id.txtpostcaption);
        sessionManager = SessionManager.getInstance(this);
        imgCapture = findViewById(R.id.btnCapture);
        icon_add_location = findViewById(R.id.icon_add_location);
        txtselectedAddress = findViewById(R.id.txtselectedAddress);
        userPostService = new UserPostService();
        followService = new FollowService();
        notificationService = new NotificationService();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagePagerAdapter(imageUris, this,this);
        recyclerView.setAdapter(adapter);
        if (sessionManager.userLoggedIn()) {
            txtpostuserName.setText(sessionManager.getUsername());
        }
        checkPermissions();
        if (sessionManager.getProfileImage() != null) {
            Uri profileImageUri = Uri.parse(sessionManager.getProfileImage()); // Convert String to Uri
            Glide.with(this)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(profileImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.user) // Replace with your image URI or resource
                    .transform(new CircleCrop())
                    .into(profileImage);
        }

        imgCapture.setOnClickListener(v -> openGallery());
        icon_add_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapDialog mapDialog = new MapDialog(CreatePostActivity.this);
                mapDialog.show();
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = txtpostcaption.getText().toString().trim();
                if (TextUtils.isEmpty(caption)) {
                    txtpostcaption.setError("Caption is required");
                    txtpostcaption.requestFocus();
                    return ;
                }
                if(tagLocation == null || tagLocation.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please tag the location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(imageUris.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Please select photo", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserPost userPost = new UserPost(sessionManager.getUserID(),caption,tagLocation,latitude,longitude,imageUris);
                userPostService.createPost(userPost, new  DataOperationCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        sendnotify(data);
                        Intent intent = new Intent(CreatePostActivity.this, ViewProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errMessage) {
                        Toast.makeText(CreatePostActivity.this, "Create post failed! Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
private void sendnotify(String postId){
    followService.getFollowAndFollowerIdsByUserId(sessionManager.getUserID(), new DataOperationCallback<List<String>>() {
        @Override
        public void onSuccess(List<String> data) {
            for (String userId : data) {
                Notification notification = new Notification(userId, sessionManager.getUserID(), "post",  " just shared a new post .Check it out!",postId);
                 notificationService.sendNotificationToUser(notification);
            }
        }

        @Override
        public void onFailure(String error) {

        }
    });
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
    @Override
    public void setLocation(LatLng latLng, String address) {
        longitude = latLng.longitude;
        latitude = latLng.latitude;
        tagLocation = address;
        txtselectedAddress.setText(tagLocation);

    }
    // region Images
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) { // Multiple images selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        addImage(imageUri);
                    }
                } else if (data.getData() != null) { // Single image selected
                    Uri imageUri = data.getData();
                    addImage(imageUri);
                }
            }
        }
    }

    private void addImage(Uri imageUri) {
        Log.d("CreatePostActivity", "Adding image: " + imageUri.toString());
        imageUris.add(imageUri);
        adapter.notifyDataSetChanged();
        myLinearLayout = findViewById(R.id.imageslayout);
        if (!imageUris.isEmpty()) {
            myLinearLayout.setVisibility(View.VISIBLE);
            recyclerView.scrollToPosition(imageUris.size() - 1);
        }
        else
        {
            myLinearLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onImageRemoved() {
        myLinearLayout.setVisibility(View.GONE);
    }
// endregion

}
