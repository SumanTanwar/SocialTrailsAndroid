package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.socialtrailsapp.ModelData.IssueWarning;
import com.example.socialtrailsapp.R;

import java.util.List;

public class AdminWarningAdapter extends RecyclerView.Adapter<AdminWarningAdapter.ReportViewHolder> {

    private Context context;
    private List<IssueWarning> issueWarningList;

    public AdminWarningAdapter(Context context, List<IssueWarning> issueWarningList) {
        this.context = context;
        this.issueWarningList = issueWarningList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_warnings, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        IssueWarning report = issueWarningList.get(position);

        // Populate the TextViews with data
        holder.reporterName.setText(report.getUsername());
        holder.reason.setText("Reason:" + report.getReason());
        holder.reportType.setText("Warning Type: " + report.getWarningtype());
        holder.issueBy.setText("Issued By: " + report.getIssuewarnby());
        holder.issueDate.setText("Issued On: " + report.getCreatedon());

        // Load user profile image
        if (report.getUserprofilepicture() != null) {
            Uri profileImageUri = Uri.parse(report.getUserprofilepicture());
            Glide.with(context)
                    .load(profileImageUri)
                    .transform(new CircleCrop())
                    .into(holder.userProfileImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.user) // Fallback image
                    .transform(new CircleCrop())
                    .into(holder.userProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return issueWarningList != null ? issueWarningList.size() : 0;
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reporterName;
        TextView reason;
        TextView reportType;
        TextView issueBy;
        TextView issueDate;
        ImageView userProfileImage;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reporterName = itemView.findViewById(R.id.reporterName);
            reason = itemView.findViewById(R.id.reason);
            reportType = itemView.findViewById(R.id.reportType);
            issueBy = itemView.findViewById(R.id.issueBy);
            issueDate = itemView.findViewById(R.id.issueDate);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
        }
    }
}
