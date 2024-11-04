package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialtrailsapp.CustomAdapter.AdminWarningAdapter;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.ModelData.IssueWarning;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.IssueWarningSevice;
import java.util.List;

public class IssueWarningActivity extends AdminBottomMenuActivity {

    private RecyclerView recyclerView;
    private AdminWarningAdapter adapter;
    private IssueWarningSevice issueWarningService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_issue_warning, findViewById(R.id.container));

        recyclerView = findViewById(R.id.recyclerView); // Assuming your layout has this ID for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        issueWarningService = new IssueWarningSevice();
        fetchIssueWarnings();
    }

    private void fetchIssueWarnings() {
        issueWarningService.fetchIssueWarnings(new DataOperationCallback<List<IssueWarning>>() {
            @Override
            public void onSuccess(List<IssueWarning> warnings) {
                adapter = new AdminWarningAdapter(IssueWarningActivity.this, warnings);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}
