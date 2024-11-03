
package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;

public class Report {
    private String reportId;
    private String reporterId;
    private String reportedId;
    private String reportingId;
    private String reporttype;
    private String reason;
    private String status;
    private String createdon;
    private String reviewedby;
    private String actiontakenby;
    private String username;
    private String userprofilepicture;

    public Report() {

    }
    public Report(String reporterid, String reportedid, String reporttype, String reason) {
        this.reporterId = reporterid;
        this.reportedId = reportedid;
        this.reporttype = reporttype;
        this.reason = reason;
        this.status = ReportStatus.PENDING.getReportStatus();
        this.createdon = Utils.getCurrentDatetime();


    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public String getReportingId() {
        return reportingId;
    }

    public void setReportingId(String reportingId) {
        this.reportingId = reportingId;
    }

    public String getReporttype() {
        return reporttype;
    }

    public void setReporttype(String reporttype) {
        this.reporttype = reporttype;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofilepicture() {
        return userprofilepicture;
    }

    public void setUserprofilepicture(String userprofilepicture) {
        this.userprofilepicture = userprofilepicture;
    }

    public String getReviewedby() {
        return reviewedby;
    }

    public void setReviewedby(String reviewedby) {
        this.reviewedby = reviewedby;
    }

    public String getActiontakenby() {
        return actiontakenby;
    }

    public void setActiontakenby(String actiontakenby) {
        this.actiontakenby = actiontakenby;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("reportId", reportId);
        result.put("reporterId", reporterId);
        result.put("reportedId", reportedId);
        result.put("reportingId", reportingId);
        result.put("reporttype", reporttype);
        result.put("reason", reason);
        result.put("status", status);
        result.put("createdon", createdon);
        result.put("reviewedby", reviewedby);
        result.put("actiontakenby", actiontakenby);
        return result;
    }

}
