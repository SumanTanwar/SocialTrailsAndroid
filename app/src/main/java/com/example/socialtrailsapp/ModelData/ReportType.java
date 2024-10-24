package com.example.socialtrailsapp.ModelData;

public enum ReportType {
    USER("user"),
    POST("post");

    private final String reporttype;

    ReportType(String role) {
        this.reporttype = role;
    }

    public String getReportType() {
        return reporttype;
    }
}