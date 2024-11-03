package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Report;

import java.util.List;

public interface IReport {
    void addReport(Report data, OperationCallback callback);
    void getReportCount(final DataOperationCallback<Integer> callback);
    void fetchReportByReportedId(String reportId, DataOperationCallback<Report> callback);
    void fetchReports(DataOperationCallback<List<Report>> callback);
    void startReviewedReport(String reportId,String reviewedby, OperationCallback callback);
    void actionTakenReport(String reportId,String actiontakenBy, OperationCallback callback);
}
