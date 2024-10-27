package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.widget.TextView;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.PostImagesService;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.Utility.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DashBoardActivity extends AdminBottomMenuActivity {

    private TextView numberofusers, numberofposts, numberofreports;
    private UserService userService;
    private UserPostService userPostService;
    private DatabaseReference reportReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.admin_activity_dashboard, findViewById(R.id.container));

        numberofusers = findViewById(R.id.numberofusers);
        numberofposts = findViewById(R.id.numberofposts);
        numberofreports = findViewById(R.id.numberofreports);
        userService = new UserService();
        userPostService = new UserPostService();
        reportReference = FirebaseDatabase.getInstance().getReference("report");


        getRegularUserList();
        getAllUserPost();
        fetchTotalReports();
    }

    private void getRegularUserList() {
        userService.getRegularUserList(new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> data) {
                if (data != null) {
                    numberofusers.setText(String.valueOf(data.size()));
                } else {
                    numberofusers.setText("0");
                }
            }

            @Override
            public void onFailure(String error) {
                numberofusers.setText("0");
            }
        });
    }

    private void getAllUserPost() {
        userService.getRegularUserList(new DataOperationCallback<List<Users>>() {
            @Override
            public void onSuccess(List<Users> data) {
                if (data != null && !data.isEmpty()) {
                    AtomicInteger totalPosts = new AtomicInteger(0);
                    AtomicInteger pendingRequests = new AtomicInteger(data.size());

                    for (Users user : data) {
                        String userId = user.getUserId();

                        // Call the service method to get posts for each user
                        userPostService.getAllUserPost(userId, new DataOperationCallback<List<UserPost>>() {
                            @Override
                            public void onSuccess(List<UserPost> posts) {
                                totalPosts.addAndGet(posts.size());
                                if (pendingRequests.decrementAndGet() == 0) {
                                    numberofposts.setText(String.valueOf(totalPosts.get()));
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                // Handle failure but still decrement the count
                                if (pendingRequests.decrementAndGet() == 0) {
                                    numberofposts.setText(String.valueOf(totalPosts.get()));
                                }
                            }
                        });
                    }
                } else {
                    numberofposts.setText("0");
                }
            }

            @Override
            public void onFailure(String error) {
                numberofposts.setText("0");
            }
        });
    }
    private void fetchTotalReports() {
        reportReference.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(DataSnapshot snapshot) {
                int reportCount = (int) snapshot.getChildrenCount();
                numberofreports.setText(String.valueOf(reportCount));
            }

            public void onCancelled(DatabaseError error) {
                numberofreports.setText("0"); // Handle error by setting reports to 0
            }
        });
    }

}
