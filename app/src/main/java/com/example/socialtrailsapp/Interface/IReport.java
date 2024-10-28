package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.Report;

public interface IReport {
    void addReport(Report data, OperationCallback callback);
    void getReportCount(final DataOperationCallback<Integer> callback);
}
