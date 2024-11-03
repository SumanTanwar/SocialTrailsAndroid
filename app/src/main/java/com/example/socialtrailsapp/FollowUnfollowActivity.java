package com.example.socialtrailsapp;

import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.CustomAdapter.GalleryImageAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.ModelData.ReportType;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.Utility.FollowService;
import com.example.socialtrailsapp.Utility.NotificationService;
import com.example.socialtrailsapp.Utility.ReportService;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;

import java.util.ArrayList;
import java.util.List;

public class FollowUnfollowActivity extends BottomMenuActivity {

    private String userId;
    private UserService userService;
    private UserPostService userPostService;
    private TextView txtprofileusername, txtuserbio, postscount, followersCount, followingsCount;
    private Button btnFollowUnfollow, btnUnfollow, btnReject, btnConfirm, btnFollowBack, btnCancelRequest;
    private SessionManager sessionManager;
    private List<UserPost> list;
    private ImageView profile_pic;
    private FollowService followService;
    private NotificationService notificationService;

    private LinearLayout followbacksection, confirmsection, followsection, unfollowsection,cancelrequestsection;
    private boolean isRequestPending = false;
    private ReportService reportService;
    private Context context;
    private Users currentUser;
    private ImageButton reportUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_follow_unfollow, findViewById(R.id.container));

        userId = getIntent().getStringExtra("intentuserId");
        userService = new UserService();
        userPostService = new UserPostService();
        txtprofileusername = findViewById(R.id.txtprofileusername);
        txtuserbio = findViewById(R.id.txtuserbio);
        postscount = findViewById(R.id.userpostscount);
        followersCount = findViewById(R.id.followers_count);
        followingsCount = findViewById(R.id.followings_count);
        profile_pic = findViewById(R.id.profile_pic);
        btnFollowUnfollow = findViewById(R.id.btnFollowUnfollow);
        btnUnfollow = findViewById(R.id.btnUnfollow);
      btnCancelRequest = findViewById(R.id.btnCancelRequest);
        btnReject = findViewById(R.id.btnReject);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnFollowBack = findViewById(R.id.btnFollowBack);
        reportUser = findViewById(R.id.reportuser);
        reportUser.setVisibility(VISIBLE);

       cancelrequestsection = findViewById(R.id.cancelrequestsection);
        followbacksection = findViewById(R.id.followbacksection);
        confirmsection = findViewById(R.id.confirmsection);
        followsection = findViewById(R.id.followsection);
        unfollowsection = findViewById(R.id.unfollowsection);

        sessionManager = SessionManager.getInstance(this);
        followService = new FollowService();
        notificationService = new NotificationService();
        context = this;


        reportService = new ReportService(); // Initialize ReportService



      //  cancelrequestsection.setVisibility(View.GONE);
        followsection.setVisibility(View.GONE);
        followbacksection.setVisibility(View.GONE);
        confirmsection.setVisibility(View.GONE);
        unfollowsection.setVisibility(View.GONE);
        cancelrequestsection.setVisibility(View.GONE);

        btnCancelRequest.setOnClickListener(v -> cancelFollowRequest());
        btnFollowUnfollow.setOnClickListener(v -> {
            if (isRequestPending) {
                cancelFollowRequest();
            } else {
                sendFollowRequest();
            }
        });
        btnUnfollow.setOnClickListener(v -> unfollowUser());
        btnConfirm.setOnClickListener(v -> confirmFollowRequest());
        btnReject.setOnClickListener(v -> rejectFollowRequest());
        btnFollowBack.setOnClickListener(v -> followBack());

        userService.getUserByID(userId, new DataOperationCallback<Users>() {
            @Override
            public void onSuccess(Users data) {
                currentUser = data; // Store the user data
                setDetail(data);
                Log.d("FollowUnfollowActivity", "Report button visibility after fetching user: " + reportUser.getVisibility());
                checkPendingFollowRequestsForCancel(data.getUserId());
               // checkFollowBack(data.getUserId());
            }

            @Override
            public void onFailure(String errMessage) {
                // Handle error
            }
        });

        reportUser.setOnClickListener(view -> {
            if (currentUser != null) {
                openReportDialog(currentUser.getUserId()); // Use the stored user data
            } else {
                Toast.makeText(FollowUnfollowActivity.this, "User data not available", Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void sendFollowRequest() {
        String currentUserId = sessionManager.getUserID(); // Get current user ID
        followService.sendFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request sent!", Toast.LENGTH_SHORT).show();
                cancelrequestsection.setVisibility(VISIBLE);
                unfollowsection.setVisibility(View.GONE);
                followsection.setVisibility(View.GONE);
                followsection.setVisibility(View.GONE);
                followbacksection.setVisibility(View.GONE);
                sendnotify(userId," has send follow request to you",currentUserId);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cancelFollowRequest() {
        String currentUserId = sessionManager.getUserID();
        followService.cancelFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request canceled!", Toast.LENGTH_SHORT).show();
                isRequestPending = false;
                followsection.setVisibility(VISIBLE);
                unfollowsection.setVisibility(View.GONE);
                confirmsection.setVisibility(View.GONE);
                followbacksection.setVisibility(View.GONE);
                cancelrequestsection.setVisibility(View.GONE);

                sendnotify(userId," has cancelled the follow request",currentUserId);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkPendingFollowRequestsForCancel(String userIdToCheck) {
        String currentUserId = sessionManager.getUserID();
        followService.checkPendingRequestsForCancel(currentUserId, userIdToCheck, new DataOperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasPendingRequest) {
                if (hasPendingRequest) {
                    confirmsection.setVisibility(View.GONE);
                    followsection.setVisibility(View.GONE);
                    unfollowsection.setVisibility(View.GONE);
                    followbacksection.setVisibility(View.GONE);
                    cancelrequestsection.setVisibility(VISIBLE);// Hide follow button
                } else {
                    checkPendingforFollowingUser(userIdToCheck);
                }
            }

            @Override
            public void onFailure(String error) {
                checkPendingforFollowingUser(userIdToCheck);
            }
        });
    }

    private void checkPendingforFollowingUser(String userIdToCheck) {
        String currentUserId = sessionManager.getUserID();
        followService.checkPendingforFollowingUser(currentUserId, userIdToCheck, new DataOperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean hasPendingRequest) {
                if (hasPendingRequest) {
                    confirmsection.setVisibility(VISIBLE);
                    followsection.setVisibility(View.GONE);
                    unfollowsection.setVisibility(View.GONE);
                    followbacksection.setVisibility(View.GONE);
                    cancelrequestsection.setVisibility(View.GONE);// Hide follow button
                } else {
                    checkFollowBack(userIdToCheck);
                }
            }

            @Override
            public void onFailure(String error) {
                checkFollowBack(userIdToCheck);
            }
        });
    }


    private void getAllUserPost(String userId) {
        userPostService.getAllUserPost(userId, new DataOperationCallback<List<UserPost>>() {
            @Override
            public void onSuccess(List<UserPost> data) {
                list = new ArrayList<>(data);
                postscount.setText(String.valueOf(list.size()));
                List<String> imageUrls = new ArrayList<>();
                List<String> postIds = new ArrayList<>();

                for (UserPost post : list) {
                    imageUrls.add(post.getUploadedImageUris().get(0).toString());
                    postIds.add(post.getPostId());
                }

                GridView gridView = findViewById(R.id.gallery_grid);
                GalleryImageAdapter adapter = new GalleryImageAdapter(FollowUnfollowActivity.this, imageUrls, postIds);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmFollowRequest() {
        String currentUserId = sessionManager.getUserID();
        followService.confirmFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request confirmed!", Toast.LENGTH_SHORT).show();
                checkFollowBack(userId);
                sendnotify(userId," has started following you",currentUserId);
//                followsection.setVisibility(View.GONE);
//                unfollowsection.setVisibility(View.GONE);
//                confirmsection.setVisibility(View.GONE);
//                followbacksection.setVisibility(VISIBLE);
//                cancelrequestsection.setVisibility(View.GONE);


            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectFollowRequest() {
        String currentUserId = sessionManager.getUserID();
        followService.rejectFollowRequest(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "Follow request rejected!", Toast.LENGTH_SHORT).show();
                followsection.setVisibility(VISIBLE);
                unfollowsection.setVisibility(View.GONE);
                confirmsection.setVisibility(View.GONE);
                followbacksection.setVisibility(View.GONE);
                cancelrequestsection.setVisibility(View.GONE);
                sendnotify(userId," has rejected the following request",currentUserId);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkFollowBack(String userIdToCheck) {
        String currentUserId = sessionManager.getUserID();
        followService.checkIfFollowed(currentUserId, userIdToCheck, new DataOperationCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFollowing) {
                if (isFollowing) {
                    followsection.setVisibility(View.GONE);
                    unfollowsection.setVisibility(View.GONE);
                    confirmsection.setVisibility(View.GONE);
                    followbacksection.setVisibility(VISIBLE);
                    cancelrequestsection.setVisibility(View.GONE);
                    followService.checkIfFollowed(userIdToCheck, currentUserId, new DataOperationCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean isFollowedBack) {
                            if (isFollowedBack) {
                                updateUIForUnFollowButton();
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            followsection.setVisibility(View.GONE);
                            unfollowsection.setVisibility(View.GONE);
                            confirmsection.setVisibility(View.GONE);
                            followbacksection.setVisibility(VISIBLE);
                            cancelrequestsection.setVisibility(View.GONE);
                        }
                    });


                } else {

                    followService.checkIfFollowed(userIdToCheck, currentUserId, new DataOperationCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean isFollowedBack) {
                            if (isFollowedBack) {
                                updateUIForUnFollowButton();
                            } else {
                                // Show follow button
                                followsection.setVisibility(VISIBLE);
                                unfollowsection.setVisibility(View.GONE);
                                confirmsection.setVisibility(View.GONE);
                                followbacksection.setVisibility(View.GONE);
                                cancelrequestsection.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            followsection.setVisibility(VISIBLE);
                            unfollowsection.setVisibility(View.GONE);
                            confirmsection.setVisibility(View.GONE);
                            followbacksection.setVisibility(View.GONE);
                            cancelrequestsection.setVisibility(View.GONE);
                        }
                    });



                }
            }

            @Override
            public void onFailure(String error) {
                followsection.setVisibility(VISIBLE);
                unfollowsection.setVisibility(View.GONE);
                confirmsection.setVisibility(View.GONE);
                followbacksection.setVisibility(View.GONE);
                cancelrequestsection.setVisibility(View.GONE);
            }
        });
    }
    private void updateUIForUnFollowButton() {
        followsection.setVisibility(View.GONE);
        unfollowsection.setVisibility(VISIBLE);
        confirmsection.setVisibility(View.GONE);
        followbacksection.setVisibility(View.GONE);
        cancelrequestsection.setVisibility(View.GONE);
    }
    private void followBack() {
        String currentUserId = sessionManager.getUserID();
        followService.confirmFollowBack(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "You are now following this user!", Toast.LENGTH_SHORT).show();
                followsection.setVisibility(View.GONE);
                unfollowsection.setVisibility(VISIBLE);
                confirmsection.setVisibility(View.GONE);
                followbacksection.setVisibility(View.GONE);
                cancelrequestsection.setVisibility(View.GONE);
                sendnotify(userId," has started following you",currentUserId);
                // unfollow button show
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfollowUser() {
        String currentUserId = sessionManager.getUserID();
        followService.unfollowUser(currentUserId, userId, new OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FollowUnfollowActivity.this, "You have unfollowed this user.", Toast.LENGTH_SHORT).show();
                followsection.setVisibility(VISIBLE);
                unfollowsection.setVisibility(View.GONE);
                confirmsection.setVisibility(View.GONE);
                followbacksection.setVisibility(View.GONE);
                cancelrequestsection.setVisibility(View.GONE);
                sendnotify(userId," has unfollowed you",currentUserId);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(FollowUnfollowActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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

    private void handleError(String error) {
        Log.e("FollowUnfollowActivity", error);
        Toast.makeText(this, "An error occurred: " + error, Toast.LENGTH_SHORT).show();
    }
    private void setDetail(Users data) {
        txtprofileusername.setText(data.getUsername());
        txtuserbio.setText(data.getBio());
        Glide.with(this)
                .load(Uri.parse(data.getProfilepicture()))
                .transform(new CircleCrop())
                .into(profile_pic);

        // Retrieve the followers and following counts
        getFollowersCount(userId);
        getFollowingCount(userId);
        getAllUserPost(userId);
    }

    private void openReportDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        EditText reportEditText = dialogView.findViewById(R.id.report_edit_text);
        Button reportButton = dialogView.findViewById(R.id.report_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        reportButton.setOnClickListener(v -> {
            String reportReason = reportEditText.getText().toString().trim();
            String reporterId = sessionManager.getUserID(); // Fetch reporter ID from session
            String reporterName = sessionManager.getUsername(); // Fetch reporter name from session

            if (!reportReason.isEmpty()) {
                reportUser(userId, reporterId, reporterName, reportReason, dialog);
            } else {
                Toast.makeText(context, "Please enter a reason for reporting.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void reportUser(String userId, String reporterId, String reporterName, String reason, AlertDialog dialog) {
        Report report = new Report(reporterId, userId, ReportType.USER.getReportType(), reason); // Updated to include reporterName
        reportService.addReport(report, new OperationCallback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
                Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(context, "Something went wrong! Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendnotify(String  notifyto,String text,String notifyBy){
        Notification notification = new Notification(notifyto, notifyBy, "follow",  " " + text ,notifyBy);
        notificationService.sendNotificationToUser(notification);
    }


}

