package com.example.socialtrailsapp.Interface;

import com.example.socialtrailsapp.ModelData.IssueWarning;

import java.util.List;

public interface IIssueWarning {
    void addWarning(IssueWarning data, OperationCallback callback);
    void getWarningCount(final DataOperationCallback<Integer> callback);
    void fetchIssueWarnings(DataOperationCallback<List<IssueWarning>> callback);

}
