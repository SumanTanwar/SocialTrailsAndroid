package com.example.socialtrailsapp.adminpanel;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.CommentAdapter;
import com.example.socialtrailsapp.CustomAdapter.PostImageAdapter;
import com.example.socialtrailsapp.CustomAdapter.PostLikeAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.PostComment;
import com.example.socialtrailsapp.ModelData.PostLike;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.MapLocationPinDialog;
import com.example.socialtrailsapp.Utility.PostCommentService;
import com.example.socialtrailsapp.Utility.PostLikeService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class AdminPostDetailActivity extends AdminBottomMenuActivity {
    private String postdetailId;
    private UserPostService userPostService;
    private PostLikeService postLikeService;
    private PostCommentService postCommentService;
    private RecyclerView commentsRecyclerView;
    private RecyclerView likesRecyclerView;

    private TextView cmtpostcnt;
    private View likesSection;
    private View commentsSection;
    TextView postlikecnt,btnRemovepost,backButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.admin_activity_post_detail, findViewById(R.id.container));
        sessionManager = SessionManager.getInstance(this);
        userPostService = new UserPostService();
        postLikeService = new PostLikeService();
        postCommentService = new PostCommentService();
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        cmtpostcnt = findViewById(R.id.cmtpostcnt);
        likesRecyclerView = findViewById(R.id.likesRecyclerView);

        // Initialize sections
        likesSection = findViewById(R.id.likesSection); // Make sure this ID exists in your layout
        commentsSection = findViewById(R.id.commentsSection); // Make sure this ID exists in your layout
         postlikecnt = findViewById(R.id.postlikecnt);
        backButton = findViewById(R.id.backButton);
        btnRemovepost = findViewById(R.id.btnRemovepost);

        Intent intent = getIntent();
        postdetailId = intent.getStringExtra("postdetailId");

        if(sessionManager.getroleType().equals(UserRole.MODERATOR.getRole()))
        {
            // moderators has no right to delete entire post
            btnRemovepost.setVisibility(View.GONE);
        }


        userPostService.getUserPostDetailById(postdetailId, new DataOperationCallback<UserPost>() {
            @Override
            public void onSuccess(UserPost data) {
                setDetail(data);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminPostDetailActivity.this, "Post load failed! Please try again later.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminPostDetailActivity.this, AdminUserViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminPostDetailActivity.this, AdminUserViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnRemovepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(AdminPostDetailActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deletePost(postdetailId);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
    private void deletePost(String postId) {
        userPostService.deleteUserPost(postId, new OperationCallback() {
            @Override
            public void onSuccess() {

                Toast.makeText(AdminPostDetailActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminPostDetailActivity.this, AdminUserViewActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(AdminPostDetailActivity.this, "Post delete failed! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setDetail(UserPost post) {
        ImageView userProfileImage = findViewById(R.id.userProfileImage);
        TextView userName = findViewById(R.id.userName);
        TextView userLocation = findViewById(R.id.userLocation);
        TextView postCaption = findViewById(R.id.postCaption);
        TextView detailrelativetime = findViewById(R.id.detailrelativetime);
        RecyclerView imagesRecyclerView = findViewById(R.id.imagesRecyclerView);


        // Load user profile image
        if (post.getUserprofilepicture() != null) {
            Uri profileImageUri = Uri.parse(post.getUserprofilepicture());
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

        userName.setText(post.getUsername());
        userLocation.setText(post.getLocation());
       // userLocation.setText("CN Tower, Toronto, Ontario");
        userLocation.setOnClickListener(view -> {

//           double latitude = 43.6426;
//           double longitude = -79.3871;
//           post.setLocation(userLocation.getText().toString());
            double latitude = post.getLatitude();
            double longitude = post.getLongitude();
            LatLng location = new LatLng(latitude, longitude);
            MapLocationPinDialog mapDialog = new MapLocationPinDialog((FragmentActivity) this, location, post.getLocation());
            mapDialog.show();
        });



        postCaption.setText(post.getCaptiontext());
        detailrelativetime.setText(Utils.getRelativeTime(post.getCreatedon()));

        // Set up images recycler view
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PostImageAdapter imageAdapter = new PostImageAdapter(this, post.getUploadedImageUris());
        imagesRecyclerView.setAdapter(imageAdapter);

        // Set like count and decide visibility of likes section
        postlikecnt.setText(String.valueOf(post.getLikecount()));
        if (post.getLikecount() == 0) {
            likesSection.setVisibility(View.GONE);
        } else {
            likesSection.setVisibility(View.VISIBLE);
            fetchLikes(postdetailId); // Fetch likes if there are any
        }

        // Set comment count and decide visibility of comments section
        cmtpostcnt.setText(String.valueOf(post.getCommentcount()));
        if (post.getCommentcount() == 0) {
            commentsSection.setVisibility(View.GONE);

        } else {
            commentsSection.setVisibility(View.VISIBLE);
            fetchComments(post.getPostId(),post.getUserId());

        }
    }

    private void fetchComments(String postId,String userid) {
        postCommentService.retrieveComments(postId, new DataOperationCallback<List<PostComment>>() {
            @Override
            public void onSuccess(List<PostComment> comments) {
                commentsRecyclerView.setLayoutManager(new LinearLayoutManager(AdminPostDetailActivity.this));
                CommentAdapter commentAdapter = new CommentAdapter(AdminPostDetailActivity.this, comments, postId,userid, new CommentAdapter.CommentActionListener() {
                    @Override
                    public void onCommentDeleted(String postId) {
                        updateCommentCount();
                    }
                });
                commentsRecyclerView.setAdapter(commentAdapter);

                // Update comment section visibility
                if (comments.size() == 0) {
                    commentsSection.setVisibility(View.GONE);

                } else {
                    commentsSection.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onFailure(String error) {
                commentsSection.setVisibility(View.GONE);

                Toast.makeText(AdminPostDetailActivity.this, "Failed to load comments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCommentCount() {
        int currentCount = Integer.parseInt(cmtpostcnt.getText().toString());
        int totalcmt = (currentCount - 1);
        cmtpostcnt.setText(String.valueOf(totalcmt));

        if (totalcmt == 0) {
            commentsSection.setVisibility(View.GONE);

        } else {
            commentsSection.setVisibility(View.VISIBLE);

        }
    }

    private void fetchLikes(String postId) {
        postLikeService.getLikesForPost(postId, new DataOperationCallback<List<PostLike>>() {
            @Override
            public void onSuccess(List<PostLike> likes) {
                likesRecyclerView.setLayoutManager(new LinearLayoutManager(AdminPostDetailActivity.this));
                PostLikeAdapter likeAdapter = new PostLikeAdapter(AdminPostDetailActivity.this, likes);
                likesRecyclerView.setAdapter(likeAdapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminPostDetailActivity.this, "Failed to load likes: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onLikeDeleted( int newLikeCount) {
        postlikecnt.setText(String.valueOf(newLikeCount));
        if (newLikeCount == 0) {
            likesSection.setVisibility(View.GONE);
        } else {
            likesSection.setVisibility(View.VISIBLE);
        }
    }

}
