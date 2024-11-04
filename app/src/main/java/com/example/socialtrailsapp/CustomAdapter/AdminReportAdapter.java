package com.example.socialtrailsapp.CustomAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.FollowUnfollowActivity;
import com.example.socialtrailsapp.Interface.OperationCallback;
import com.example.socialtrailsapp.ModelData.IssueWarning;
import com.example.socialtrailsapp.ModelData.Notification;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.ModelData.ReportType;
import com.example.socialtrailsapp.ModelData.UserPost;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.UserPostDetailActivity;
import com.example.socialtrailsapp.Utility.IssueWarningSevice;
import com.example.socialtrailsapp.Utility.NotificationService;
import com.example.socialtrailsapp.Utility.ReportService;
import com.example.socialtrailsapp.Interface.DataOperationCallback;
import com.example.socialtrailsapp.Utility.SessionManager;
import com.example.socialtrailsapp.Utility.UserPostService;
import com.example.socialtrailsapp.adminpanel.AdminPostDetailActivity;
import com.example.socialtrailsapp.adminpanel.AdminUserViewActivity;

import java.util.List;

public class AdminReportAdapter extends RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder> {

    private Context context;
    private List<Report> reportList;
    private SessionManager sessionManager;
    private IssueWarningSevice issueWarningSevice;
    private NotificationService notificationService;
    private UserPostService userPostService;

    public AdminReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
        issueWarningSevice = new IssueWarningSevice();
        sessionManager = SessionManager.getInstance(context);
        notificationService = new NotificationService();
        userPostService = new UserPostService();
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

        holder.btnissuewarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ReportType.POST.getReportType().equalsIgnoreCase(report.getReporttype()))
                {
                    userPostService.getUserPostDetailById(report.getReportedId(), new DataOperationCallback<UserPost>() {
                        @Override
                        public void onSuccess(UserPost data) {
                            openWarningDialog(report.getReportedId(),data.getUserId(),report.getReporttype());
                        }

                        @Override
                        public void onFailure(String error) {

                        }
                    });
                }
                else
                {
                    openWarningDialog(report.getReportedId(),report.getReportedId(),report.getReporttype());
                }

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
        ImageView eyeIcon,userProfileImage,btnissuewarning;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reporterName = itemView.findViewById(R.id.reporterName);
            status = itemView.findViewById(R.id.status);
            reportType = itemView.findViewById(R.id.reportType);
            reportDate = itemView.findViewById(R.id.reportDate);
            eyeIcon = itemView.findViewById(R.id.eyeIcon);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            btnissuewarning = itemView.findViewById(R.id.btnissuewarning);
        }
    }

    private void openWarningDialog(String issueWarnId,String issueWarnto, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        EditText reportEditText = dialogView.findViewById(R.id.report_edit_text);
        Button reportButton = dialogView.findViewById(R.id.report_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        reportButton.setOnClickListener(v -> {
            String reportReason = reportEditText.getText().toString().trim();
            if (!reportReason.isEmpty()) {
                issueWarning(issueWarnId,issueWarnto,type, reportReason,dialog);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a reason for issue warning.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void issueWarning(String issueWarnId,String issueWarnto,String type, String reason, AlertDialog dialog) {
        IssueWarning issue = new IssueWarning(sessionManager.getUsername(),issueWarnto,issueWarnId,type, reason);
        issueWarningSevice.addWarning(issue, new OperationCallback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();

                Notification notification = new Notification(issueWarnto, sessionManager.getUserID(), type,  reason,issueWarnId);
                notificationService.sendNotificationToUser(notification);
                Toast.makeText(context, "Warning successfully issued!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errMessage) {
                Toast.makeText(context, "Something wrong ! Please try after some time", Toast.LENGTH_SHORT).show();
            }
        });
    }


}