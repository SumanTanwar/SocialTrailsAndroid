package com.example.socialtrailsapp.adminpanel;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.CustomAdapter.AdminReportAdapter;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.Utility.ReportService;
import com.example.socialtrailsapp.Interface.DataOperationCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminReportViewActivity extends AdminBottomMenuActivity {

    private RecyclerView recyclerViewReports;
    private AdminReportAdapter reportsAdapter;
    private List<Report> reportsList;

    private ReportService reportService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_admin_report_view, findViewById(R.id.container));

        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        reportsList = new ArrayList<>();

        reportsAdapter = new AdminReportAdapter(this, reportsList);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReports.setAdapter(reportsAdapter);


        reportService = new ReportService();

        fetchReports();
    }

    private void fetchReports() {
        reportService.fetchReports(new DataOperationCallback<List<Report>>() {
            @Override
            public void onSuccess(List<Report> reports) {
                reportsList.clear();
                reportsList.addAll(reports);
                reportsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminReportViewActivity.this, "Error fetching reports: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}