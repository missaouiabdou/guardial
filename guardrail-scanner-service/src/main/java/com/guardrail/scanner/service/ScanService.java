package com.guardrail.scanner.service;

import com.guardrail.scanner.domain.Scan;
import com.guardrail.scanner.domain.ScanStatus;
import com.guardrail.scanner.domain.Vulnerability;
import com.guardrail.scanner.dto.ScanRequest;
import com.guardrail.scanner.dto.ScanResponse;
import com.guardrail.scanner.dto.VulnerabilityDto;
import com.guardrail.scanner.repository.ScanRepository;
import com.guardrail.scanner.repository.VulnerabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScanService {

    private final ScanRepository scanRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final ScannerParserService parserService;

    public ScanService(ScanRepository scanRepository,
                       VulnerabilityRepository vulnerabilityRepository,
                       ScannerParserService parserService) {
        this.scanRepository = scanRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.parserService = parserService;
    }

    public ScanResponse createAndProcessScan(ScanRequest request) {
        Scan scan = new Scan(
                request.getProjectName(),
                request.getCommitSha(),
                request.getBranch(),
                request.getScannerType() != null ? request.getScannerType() : "brakeman"
        );
        scan.setStatus(ScanStatus.RUNNING);
        scan = scanRepository.save(scan);

        try {
            if (request.getRawReportJson() != null && !request.getRawReportJson().isBlank()) {
                List<Vulnerability> vulnerabilities = parserService.parseBrakemanReport(request.getRawReportJson());
                for (Vulnerability v : vulnerabilities) {
                    scan.addVulnerability(v);
                }
            }

            scan.setStatus(ScanStatus.COMPLETED);
            scan.setCompletedAt(LocalDateTime.now());
        } catch (Exception e) {
            scan.setStatus(ScanStatus.FAILED);
            scan.setCompletedAt(LocalDateTime.now());
        }

        scan = scanRepository.save(scan);
        return ScanResponse.fromEntity(scan, true);
    }

    @Transactional(readOnly = true)
    public List<ScanResponse> getAllScans() {
        return scanRepository.findAll().stream()
                .map(scan -> ScanResponse.fromEntity(scan, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ScanResponse getScanById(Long id) {
        Scan scan = scanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scan not found with id: " + id));
        return ScanResponse.fromEntity(scan, true);
    }

    @Transactional(readOnly = true)
    public List<ScanResponse> getScansByProject(String projectName) {
        return scanRepository.findByProjectNameOrderByCreatedAtDesc(projectName).stream()
                .map(scan -> ScanResponse.fromEntity(scan, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VulnerabilityDto> getVulnerabilitiesForScan(Long scanId) {
        return vulnerabilityRepository.findByScanId(scanId).stream()
                .map(VulnerabilityDto::fromEntity)
                .collect(Collectors.toList());
    }
}
