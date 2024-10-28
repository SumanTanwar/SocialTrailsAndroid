
package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.Utils;

public class Report {
    private String reportid;
    private String reporterid;
    private String reportedid;
    private String reportingid;
    private String reporttype;
    private String reason;
    private String status;
    private String createdon;
    private String reporterName; // name


    public  Report(){

    }
    public Report(String reporterid, String reportedid, String reporttype, String reason, String reporterName) {
        this.reporterid = reporterid;
        this.reportedid = reportedid;
        this.reporttype = reporttype;
        this.reason = reason;
        this.status = ReportStatus.PENDING.getReportStatus();
        this.createdon = Utils.getCurrentDatetime();
        this.reporterName = reporterName; // Initialize reporter name

    }
    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
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
}
