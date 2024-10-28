
package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;

public class Report {
    private String reportid;
    private String reporterid;
    private String reportedid;
    private String reportingid;
    private String reporttype;
    private String reason;
    private String status;
    private String createdon;

    private  String username;
    private String userprofilepicture;

    public  Report(){

    }
    public Report(String reporterid, String reportedid, String reporttype, String reason) {
        this.reporterid = reporterid;
        this.reportedid = reportedid;
        this.reporttype = reporttype;
        this.reason = reason;
        this.status = ReportStatus.PENDING.getReportStatus();
        this.createdon = Utils.getCurrentDatetime();


    }


    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
    }

    public String getReporterid() {
        return reporterid;
    }

    public void setReporterid(String reporterid) {
        this.reporterid = reporterid;
    }

    public String getReportedid() {
        return reportedid;
    }

    public void setReportedid(String reportedid) {
        this.reportedid = reportedid;
    }

    public String getReportingid() {
        return reportingid;
    }

    public void setReportingid(String reportingid) {
        this.reportingid = reportingid;
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

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("reportid", reportid);
        result.put("reporterid", reporterid);
        result.put("reportedid", reportedid);
        result.put("reportingid", reportingid);
        result.put("reporttype", reporttype);
        result.put("reason", reason);
        result.put("status", status);
        result.put("createdon", createdon);
        return result;
    }
}
