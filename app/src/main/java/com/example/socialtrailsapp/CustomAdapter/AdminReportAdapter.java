package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.ModelData.ReportType;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.UserPostDetailActivity;
import com.example.socialtrailsapp.Utility.ReportService;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.adminpanel.AdminPostDetailActivity;
import com.example.socialtrailsapp.adminpanel.AdminUserViewActivity;

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

        holder.status.setText("Status: " + report.getStatus());
        holder.reportType.setText("Report Type: " + report.getReporttype());
        holder.reportDate.setText("Reported On: " + report.getCreatedon());

        if (report.getUserprofilepicture() != null) {
            Uri profileImageUri = Uri.parse(report.getUserprofilepicture());
            Glide.with(context)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(holder.userProfileImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.user)
                    .transform(new CircleCrop())
                    .into(holder.userProfileImage);
        }

        holder.reporterName.setText(report.getUsername());
        holder.eyeIcon.setOnClickListener(v -> {

            String reportType = report.getReporttype();
            String reportId = report.getReportedId(); // General report ID


            // Redirect based on report type
            if (ReportType.POST.getReportType().equalsIgnoreCase(reportType)) {
                Intent intent = new Intent(context, AdminPostDetailActivity.class);
                intent.putExtra("postdetailId", reportId); // Pass the reportId as post ID
                intent.putExtra("reportId", report.getReportId()); // Pass the reportId as post ID
                context.startActivity(intent);
            } else if (ReportType.USER.getReportType().equalsIgnoreCase(reportType)) {
                Intent intent = new Intent(context, AdminUserViewActivity.class);
                intent.putExtra("intentuserId", reportId); // Pass the reportId as user ID
                intent.putExtra("reportId", report.getReportId()); // Pass the reportId as user ID
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No valid action for this report type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList != null ? reportList.size() : 0; // Safe check for null
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reporterName;
        TextView status;
        TextView reportType;
        TextView reportDate;
        ImageView eyeIcon,userProfileImage;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reporterName = itemView.findViewById(R.id.reporterName);
            status = itemView.findViewById(R.id.status);
            reportType = itemView.findViewById(R.id.reportType);
            reportDate = itemView.findViewById(R.id.reportDate);
            eyeIcon = itemView.findViewById(R.id.eyeIcon);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
        }
    }
}