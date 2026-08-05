package com.rapid7.nexpose.nsc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScanRepository extends JpaRepository<ScanRecord, Long> {

    List<ScanRecord> findAllByOrderByStartedAtDesc();

    @Query("select count(v) from VulnerabilityRecord v where v.asset.scan.id = :scanId")
    long countVulnerabilities(Long scanId);
}
