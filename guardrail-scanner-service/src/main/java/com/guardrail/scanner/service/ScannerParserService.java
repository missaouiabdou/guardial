package com.guardrail.scanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardrail.scanner.domain.Severity;
import com.guardrail.scanner.domain.Vulnerability;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScannerParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Vulnerability> parseBrakemanReport(String jsonContent) {
        List<Vulnerability> vulnerabilities = new ArrayList<>();
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            return vulnerabilities;
        }

        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            JsonNode warnings = root.path("warnings");

            if (warnings.isArray()) {
                for (JsonNode warning : warnings) {
                    Vulnerability vuln = new Vulnerability();

                    String warningType = warning.path("warning_type").asText("Security Warning");
                    String message = warning.path("message").asText();
                    String confidence = warning.path("confidence").asText();
                    String file = warning.path("file").asText();
                    int line = warning.path("line").asInt(0);
                    String code = warning.path("code").asText();
                    String fingerprint = warning.path("fingerprint").asText();
                    String link = warning.path("link").asText();

                    vuln.setWarningType(warningType);
                    vuln.setMessage(message);
                    vuln.setFile(file);
                    vuln.setLine(line > 0 ? line : null);
                    vuln.setCode(code);
                    vuln.setFingerprint(fingerprint);
                    vuln.setSolution(link);
                    vuln.setSeverity(mapBrakemanConfidence(confidence));

                    JsonNode cweArray = warning.path("cwe_id");
                    if (cweArray.isArray() && !cweArray.isEmpty()) {
                        vuln.setCweId("CWE-" + cweArray.get(0).asText());
                    }

                    vulnerabilities.add(vuln);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Brakeman report: " + e.getMessage(), e);
        }

        return vulnerabilities;
    }

    private Severity mapBrakemanConfidence(String confidence) {
        if (confidence == null) return Severity.MEDIUM;
        return switch (confidence.trim().toLowerCase()) {
            case "high" -> Severity.HIGH;
            case "medium" -> Severity.MEDIUM;
            case "weak", "low" -> Severity.LOW;
            default -> Severity.INFO;
        };
    }
}
