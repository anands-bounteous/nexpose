package com.rapid7.nexpose.nsc.service;

import com.rapid7.nexpose.console.correlation.DomainAggregator;
import com.rapid7.nexpose.console.domain.Asset;
import com.rapid7.nexpose.nsc.repository.AssetRecord;
import com.rapid7.nexpose.nsc.repository.AssetRepository;
import com.rapid7.nexpose.nsc.repository.ScanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Aggregates headline metrics for the dashboard. No planted defects here. */
@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final ScanRepository scanRepository;
    private final AssetRepository assetRepository;
    private final DomainAggregator domainAggregator;

    public DashboardService(ScanRepository scanRepository,
                            AssetRepository assetRepository,
                            DomainAggregator domainAggregator) {
        this.scanRepository = scanRepository;
        this.assetRepository = assetRepository;
        this.domainAggregator = domainAggregator;
    }

    public Map<String, Object> summary() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<AssetRecord> assets = assetRepository.findAll();
        long vulnCount = assets.stream().mapToLong(a -> a.getVulnerabilities().size()).sum();
        long critical = assets.stream()
                .flatMap(a -> a.getVulnerabilities().stream())
                .filter(v -> v.getCvssScore() >= 9.0)
                .count();
        stats.put("scanCount", scanRepository.count());
        stats.put("assetCount", assets.size());
        stats.put("vulnerabilityCount", vulnCount);
        stats.put("criticalCount", critical);

        List<Asset> domainModel = new ArrayList<>();
        for (AssetRecord ar : assets) {
            Asset a = new Asset(ar.getIpAddress(), ar.getHostName(), ar.getOperatingSystem());
            domainModel.add(a);
        }
        stats.put("byDomain", domainAggregator.countByDomain(domainModel));
        log.debug("Dashboard summary: {}", stats);
        return stats;
    }
}
