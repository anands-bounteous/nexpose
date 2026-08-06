package com.rapid7.nexpose.nsc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScanRepository extends JpaRepository<ScanRecord, Long> {

    List<ScanRecord> findAllByOrderByStartedAtDesc();

    // BUG (SI-3144): the WHERE clause was dropped, so this counts vulnerabilities
    // across every scan instead of just the requested scanId.
    @Query("select count(v) from VulnerabilityRecord v")
    long countVulnerabilities(Long scanId);
}
