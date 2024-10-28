package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.widget.TextView;

import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.ModelData.UserRole;
import com.example.socialtrailsapp.ModelData.Users;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.PostImagesService;
import com.example.socialtrailsapp.Utility.ReportService;
import com.example.socialtrailsapp.Utility.SessionManager;
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

    private TextView numberofusers, numberofposts, numberofreports,userRoleText;
    private UserService userService;
    private UserPostService userPostService;
    private ReportService reportService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.admin_activity_dashboard, findViewById(R.id.container));

        numberofusers = findViewById(R.id.numberofusers);
        numberofposts = findViewById(R.id.numberofposts);
        numberofreports = findViewById(R.id.numberofreports);
        userRoleText = findViewById(R.id.userRoleText);
        userService = new UserService();
        userPostService = new UserPostService();
        reportService = new ReportService();
        sessionManager = SessionManager.getInstance(this);

        if(sessionManager.getroleType().equals(UserRole.MODERATOR.getRole()))
        {
            userRoleText.setText("MODERATOR");
        }
        else
        {
            userRoleText.setText("ADMIN");
        }
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
        userPostService.getPostCount(new DataOperationCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                numberofposts.setText(String.valueOf(data));
            }

            @Override
            public void onFailure(String error) {
                numberofposts.setText("0");
            }
        });


    }
    private void fetchTotalReports() {
        reportService.getReportCount(new DataOperationCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                numberofreports.setText(String.valueOf(data));
            }

            @Override
            public void onFailure(String error) {
                numberofreports.setText("0");
            }
        });

    }

}
