package com.rapid7.nexpose.nsc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<AssetRecord, Long> {
    List<AssetRecord> findByScanId(Long scanId);
}
