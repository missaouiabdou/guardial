package com.guardrail.scanner.controller;

import com.guardrail.scanner.dto.ScanRequest;
import com.guardrail.scanner.dto.ScanResponse;
import com.guardrail.scanner.dto.VulnerabilityDto;
import com.guardrail.scanner.service.ScanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scans")
@Tag(name = "Scans", description = "Endpoints for managing security scans and findings")
@CrossOrigin(origins = "*")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping
    @Operation(summary = "Submit and process a security scan report")
    public ResponseEntity<ScanResponse> createScan(@Valid @RequestBody ScanRequest request) {
        ScanResponse response = scanService.createAndProcessScan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all scans")
    public ResponseEntity<List<ScanResponse>> getAllScans() {
        return ResponseEntity.ok(scanService.getAllScans());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get scan details with full vulnerability findings by ID")
    public ResponseEntity<ScanResponse> getScanById(@PathVariable Long id) {
        return ResponseEntity.ok(scanService.getScanById(id));
    }

    @GetMapping("/project/{projectName}")
    @Operation(summary = "Get all scans for a specific project")
    public ResponseEntity<List<ScanResponse>> getScansByProject(@PathVariable String projectName) {
        return ResponseEntity.ok(scanService.getScansByProject(projectName));
    }

    @GetMapping("/{id}/vulnerabilities")
    @Operation(summary = "Get all vulnerabilities detected in a specific scan")
    public ResponseEntity<List<VulnerabilityDto>> getVulnerabilitiesForScan(@PathVariable Long id) {
        return ResponseEntity.ok(scanService.getVulnerabilitiesForScan(id));
    }
}
