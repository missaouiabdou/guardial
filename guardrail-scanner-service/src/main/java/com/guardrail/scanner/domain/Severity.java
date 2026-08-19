package com.guardrail.scanner.domain;

public enum Severity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    INFO;

    public static Severity fromString(String val) {
        if (val == null) return INFO;
        try {
            return Severity.valueOf(val.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return INFO;
        }
    }
}
