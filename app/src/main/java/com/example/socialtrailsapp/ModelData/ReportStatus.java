package com.example.socialtrailsapp.ModelData;

public enum ReportStatus {
    PENDING("pending"),
    REVIEW("reviewing"),
    ACTIONED("actioned");

    private final String reportstatus;

    ReportStatus(String role) {
        this.reportstatus = role;
    }

    public String getReportStatus() {
        return reportstatus;
    }
}
