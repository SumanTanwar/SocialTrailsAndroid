
package com.example.socialtrailsapp.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.socialtrailsapp.ModelData.Report;
import com.example.socialtrailsapp.R;
import com.example.socialtrailsapp.adminpanel.AdminReportDetailsActivity;
import java.util.List;

public class AdminReportAdapter extends RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder> {

    private Context context;
    private List<Report> reportsList;

    public AdminReportAdapter(Context context, List<Report> reportsList) {
        this.context = context;
        this.reportsList = reportsList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reports, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportsList.get(position);

        // Show essential details
        holder.textReportId.setText("Report ID: " + report.getReportid());
        holder.textReportType.setText("Type: " + report.getReporttype());
        holder.textStatus.setText("Status: " + report.getStatus());

        // Set the onClickListener for the View Details button
        holder.buttonViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminReportDetailsActivity.class);
            intent.putExtra("reportId", report.getReportid());
            intent.putExtra("reporterId", report.getReporterid());
            intent.putExtra("reportedId", report.getReportedid());
            intent.putExtra("reportType", report.getReporttype());
            intent.putExtra("reason", report.getReason());
            intent.putExtra("status", report.getStatus());
            intent.putExtra("createdOn", report.getCreatedon());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView textReportId;
        TextView textReportType;
        TextView textStatus;
        Button buttonViewDetails;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            textReportId = itemView.findViewById(R.id.textReportId);
            textReportType = itemView.findViewById(R.id.textReportType);
            textStatus = itemView.findViewById(R.id.textStatus);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
        }
    }
}
