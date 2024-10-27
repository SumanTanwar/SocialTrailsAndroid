package com.example.socialtrailsapp.adminpanel;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.socialtrailsapp.R;

public class AdminReportDetailsActivity extends AdminBottomMenuActivity {

    private TextView textReportId, textReporterId, textReportedId, textReportType, textReason, textStatus, textCreatedOn;
    private Button buttonApprove, buttonReject, buttonIssueWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_admin_report_details); // Set your layout file
        getLayoutInflater().inflate(R.layout.activity_admin_report_details, findViewById(R.id.container));

        // Initialize Views
        textReportId = findViewById(R.id.textReportId);
        textReporterId = findViewById(R.id.textReporterId);
        textReportedId = findViewById(R.id.textReportedId);
        textReportType = findViewById(R.id.textReportType);
        textReason = findViewById(R.id.textReason);
        textStatus = findViewById(R.id.textStatus);
        textCreatedOn = findViewById(R.id.textCreatedOn);
        buttonApprove = findViewById(R.id.buttonApprove);
        buttonReject = findViewById(R.id.buttonReject);
        buttonIssueWarning = findViewById(R.id.buttonIssueWarning);


        String reportId = getIntent().getStringExtra("reportId");
        String reporterId = getIntent().getStringExtra("reporterId");
        String reportedId = getIntent().getStringExtra("reportedId");
        String reportType = getIntent().getStringExtra("reportType");
        String reason = getIntent().getStringExtra("reason");
        String status = getIntent().getStringExtra("status");
        String createdOn = getIntent().getStringExtra("createdOn");



        String reportIdText = "Report ID: " + reportId;
        SpannableString reportIdSpannable = new SpannableString(reportIdText);
        reportIdSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Report ID: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textReportId.setText(reportIdSpannable);


        String reporterIdText = "Reporter ID: " + reporterId;
        SpannableString reporterIdSpannable = new SpannableString(reporterIdText);
        reporterIdSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Reporter ID: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textReporterId.setText(reporterIdSpannable);

        String reportedIdText = "Reported ID: " + reportedId;
        SpannableString reportedIdSpannable = new SpannableString(reportedIdText);
        reportedIdSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Reported ID: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textReportedId.setText(reportedIdSpannable);

        String reportTypeText = "Type: " + reportType;
        SpannableString reportTypeSpannable = new SpannableString(reportTypeText);
        reportTypeSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Type: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textReportType.setText(reportTypeSpannable);

        String reasonText = "Reason: " + reason;
        SpannableString reasonSpannable = new SpannableString(reasonText);
        reasonSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Reason: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textReason.setText(reasonSpannable);

        String statusText = "Status: " + status;
        SpannableString statusSpannable = new SpannableString(statusText);
        statusSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Status: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textStatus.setText(statusSpannable);

        String createdOnText = "Created On: " + createdOn;
        SpannableString createdOnSpannable = new SpannableString(createdOnText);
        createdOnSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, "Created On: ".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textCreatedOn.setText(createdOnSpannable);


        buttonApprove.setOnClickListener(v -> {

            Toast.makeText(this, "Report Approved", Toast.LENGTH_SHORT).show();
        });

        buttonReject.setOnClickListener(v -> {

            Toast.makeText(this, "Report Rejected", Toast.LENGTH_SHORT).show();
        });

        buttonIssueWarning.setOnClickListener(v -> {
            Toast.makeText(this, "Warning Issued", Toast.LENGTH_SHORT).show();
        });
    }
}
