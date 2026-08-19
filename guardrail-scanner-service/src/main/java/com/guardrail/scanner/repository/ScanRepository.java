package com.guardrail.scanner.repository;

import com.guardrail.scanner.domain.Scan;
import com.guardrail.scanner.domain.ScanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {
    List<Scan> findByProjectNameOrderByCreatedAtDesc(String projectName);
    List<Scan> findByStatus(ScanStatus status);
}
