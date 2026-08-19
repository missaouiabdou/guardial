package com.guardrail.scanner.dto;

import com.guardrail.scanner.domain.Scan;
import com.guardrail.scanner.domain.ScanStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScanResponse {
    private Long id;
    private String projectName;
    private String commitSha;
    private String branch;
    private ScanStatus status;
    private String scannerType;
    private int totalVulnerabilities;
    private int criticalCount;
    private int highCount;
    private int mediumCount;
    private int lowCount;
    private int infoCount;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<VulnerabilityDto> vulnerabilities = new ArrayList<>();

    public ScanResponse() {}

    public static ScanResponse fromEntity(Scan scan, boolean includeVulnerabilities) {
        ScanResponse response = new ScanResponse();
        response.setId(scan.getId());
        response.setProjectName(scan.getProjectName());
        response.setCommitSha(scan.getCommitSha());
        response.setBranch(scan.getBranch());
        response.setStatus(scan.getStatus());
        response.setScannerType(scan.getScannerType());
        response.setCriticalCount(scan.getCriticalCount());
        response.setHighCount(scan.getHighCount());
        response.setMediumCount(scan.getMediumCount());
        response.setLowCount(scan.getLowCount());
        response.setInfoCount(scan.getInfoCount());
        response.setTotalVulnerabilities(
                scan.getCriticalCount() + scan.getHighCount() + scan.getMediumCount() + scan.getLowCount() + scan.getInfoCount()
        );
        response.setCreatedAt(scan.getCreatedAt());
        response.setCompletedAt(scan.getCompletedAt());

        if (includeVulnerabilities && scan.getVulnerabilities() != null) {
            response.setVulnerabilities(
                    scan.getVulnerabilities().stream()
                            .map(VulnerabilityDto::fromEntity)
                            .collect(Collectors.toList())
            );
        }
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getCommitSha() { return commitSha; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public ScanStatus getStatus() { return status; }
    public void setStatus(ScanStatus status) { this.status = status; }

    public String getScannerType() { return scannerType; }
    public void setScannerType(String scannerType) { this.scannerType = scannerType; }

    public int getTotalVulnerabilities() { return totalVulnerabilities; }
    public void setTotalVulnerabilities(int totalVulnerabilities) { this.totalVulnerabilities = totalVulnerabilities; }

    public int getCriticalCount() { return criticalCount; }
    public void setCriticalCount(int criticalCount) { this.criticalCount = criticalCount; }

    public int getHighCount() { return highCount; }
    public void setHighCount(int highCount) { this.highCount = highCount; }

    public int getMediumCount() { return mediumCount; }
    public void setMediumCount(int mediumCount) { this.mediumCount = mediumCount; }

    public int getLowCount() { return lowCount; }
    public void setLowCount(int lowCount) { this.lowCount = lowCount; }

    public int getInfoCount() { return infoCount; }
    public void setInfoCount(int infoCount) { this.infoCount = infoCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<VulnerabilityDto> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<VulnerabilityDto> vulnerabilities) { this.vulnerabilities = vulnerabilities; }
}
