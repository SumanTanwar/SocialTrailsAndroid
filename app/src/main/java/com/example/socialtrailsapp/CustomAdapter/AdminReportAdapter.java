package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.UserPostDetailActivity;

import java.util.List;

public class AdminReportAdapter extends RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder> {

    private Context context;
    private List<Report> reportList;

    public AdminReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reports, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);


        holder.reporterName.setText("Reporter Name:   " + report.getReporterName());
        holder.status.setText("Status:    " + report.getStatus());
        holder.reportType.setText("Report Type:    " + report.getReporttype());
        holder.reportDate.setText("Date:    " + report.getCreatedon());


//        holder.eyeIcon.setOnClickListener(v -> {
//            if (report == null) {
//                Toast.makeText(context, "Report is null", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            String reportType = report.getReporttype();
//            String reportId = report.getReportid(); // General report ID
//
//            if (reportId == null) {
//                Toast.makeText(context, "Report ID is null", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Redirect based on report type
//            if ("post".equalsIgnoreCase(reportType)) {
//                Intent intent = new Intent(context, UserPostDetailActivity.class);
//                intent.putExtra("postdetailId", reportId); // Pass the reportId as post ID
//                context.startActivity(intent);
//            } else if ("user".equalsIgnoreCase(reportType)) {
//                Intent intent = new Intent(context, FollowUnfollowActivity.class);
//                intent.putExtra("userId", reportId); // Pass the reportId as user ID
//                context.startActivity(intent);
//            } else {
//                Toast.makeText(context, "No valid action for this report type", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


        @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reporterName;
        TextView status;
        TextView reportType;
        TextView reportDate;
        ImageView eyeIcon;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reporterName = itemView.findViewById(R.id.reporterName);
            status = itemView.findViewById(R.id.status);
            reportType = itemView.findViewById(R.id.reportType);
            reportDate = itemView.findViewById(R.id.reportDate);
            eyeIcon = itemView.findViewById(R.id.eyeIcon);
        }
    }
}
