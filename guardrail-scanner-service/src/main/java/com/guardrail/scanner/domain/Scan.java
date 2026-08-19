package com.guardrail.scanner.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scans")
public class Scan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    private String commitSha;
    private String branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanStatus status = ScanStatus.PENDING;

    private String scannerType;

    private int criticalCount = 0;
    private int highCount = 0;
    private int mediumCount = 0;
    private int lowCount = 0;
    private int infoCount = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Vulnerability> vulnerabilities = new ArrayList<>();

    public Scan() {}

    public Scan(String projectName, String commitSha, String branch, String scannerType) {
        this.projectName = projectName;
        this.commitSha = commitSha;
        this.branch = branch;
        this.scannerType = scannerType;
        this.createdAt = LocalDateTime.now();
        this.status = ScanStatus.PENDING;
    }

    public void addVulnerability(Vulnerability vulnerability) {
        vulnerabilities.add(vulnerability);
        vulnerability.setScan(this);
        recalculateCounts();
    }

    public void recalculateCounts() {
        this.criticalCount = 0;
        this.highCount = 0;
        this.mediumCount = 0;
        this.lowCount = 0;
        this.infoCount = 0;

        for (Vulnerability v : vulnerabilities) {
            if (v.getSeverity() == null) continue;
            switch (v.getSeverity()) {
                case CRITICAL -> this.criticalCount++;
                case HIGH -> this.highCount++;
                case MEDIUM -> this.mediumCount++;
                case LOW -> this.lowCount++;
                case INFO -> this.infoCount++;
            }
        }
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

    public List<Vulnerability> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(List<Vulnerability> vulnerabilities) { this.vulnerabilities = vulnerabilities; }
}
