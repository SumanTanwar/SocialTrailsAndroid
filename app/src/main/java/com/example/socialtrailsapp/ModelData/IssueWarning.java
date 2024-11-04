package com.example.socialtrailsapp.ModelData;

import com.example.socialtrailsapp.Utility.IssueWarningSevice;
import com.example.socialtrailsapp.Utility.Utils;

import java.util.HashMap;
import java.util.Map;

public class IssueWarning {
    private String issuewarningId;
    private String issuewarnby;
    private String issuewarnto;
    private String issuewarnId;
    private String warningtype;
    private String reason;
    private String createdon;
    private String username;
    private String userprofilepicture;

    public IssueWarning()
    {}
    public IssueWarning(String issuewarnby,String issuewarnto, String issuewarnId, String warningtype, String reason) {
        this.issuewarnby = issuewarnby;
        this.issuewarnId = issuewarnId;
        this.issuewarnto = issuewarnto;
        this.warningtype = warningtype;
        this.reason = reason;
        this.createdon = Utils.getCurrentDatetime();
    }

    public String getIssuewarningId() {
        return issuewarningId;
    }

    public void setIssuewarningId(String issuewarningId) {
        this.issuewarningId = issuewarningId;
    }

    public String getIssuewarnby() {
        return issuewarnby;
    }

    public void setIssuewarnby(String issuewarnby) {
        this.issuewarnby = issuewarnby;
    }

    public String getIssuewarnId() {
        return issuewarnId;
    }

    public void setIssuewarnId(String issuewarnId) {
        this.issuewarnId = issuewarnId;
    }

    public String getWarningtype() {
        return warningtype;
    }

    public void setWarningtype(String warningtype) {
        this.warningtype = warningtype;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getIssuewarnto() {
        return issuewarnto;
    }

    public void setIssuewarnto(String issuewarnto) {
        this.issuewarnto = issuewarnto;
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
        result.put("issuewarningId", issuewarningId);
        result.put("issuewarnby", issuewarnby);
        result.put("issuewarnto", issuewarnto);
        result.put("issuewarnId", issuewarnId);
        result.put("warningtype", warningtype);
        result.put("reason", reason);
        result.put("createdon", createdon);
        return result;
    }
}
