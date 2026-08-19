package com.guardrail.scanner.dto;

import jakarta.validation.constraints.NotBlank;

public class ScanRequest {

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String commitSha;
    private String branch;
    private String scannerType = "brakeman";
    private String rawReportJson;

    public ScanRequest() {}

    public ScanRequest(String projectName, String commitSha, String branch, String scannerType) {
        this.projectName = projectName;
        this.commitSha = commitSha;
        this.branch = branch;
        this.scannerType = scannerType;
    }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getCommitSha() { return commitSha; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getScannerType() { return scannerType; }
    public void setScannerType(String scannerType) { this.scannerType = scannerType; }

    public String getRawReportJson() { return rawReportJson; }
    public void setRawReportJson(String rawReportJson) { this.rawReportJson = rawReportJson; }
}
