
package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialtrailsapp.CustomAdapter.AdminReportAdapter;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminReportViewActivity extends AdminBottomMenuActivity {

    private RecyclerView recyclerViewReports;
    private AdminReportAdapter reportsAdapter;
    private List<Report> reportsList;
    private DatabaseReference reportReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_report_view, findViewById(R.id.container));

        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        reportsList = new ArrayList<>();

        // Initialize the adapter
        reportsAdapter = new AdminReportAdapter(this, reportsList);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReports.setAdapter(reportsAdapter);

        // Initialize Firebase Database reference
        reportReference = FirebaseDatabase.getInstance().getReference("report");

        // Fetch reports from Firebase
        fetchReports();
    }

    private void fetchReports() {
        reportReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reportsList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Report report = dataSnapshot.getValue(Report.class);
                        if (report != null) {
                            reportsList.add(report);
                        }
                    }

                    reportsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminReportViewActivity.this, "No reports found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminReportViewActivity.this, "Error fetching reports: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
