package com.rapid7.nexpose.nsc.service;

import com.rapid7.nexpose.console.correlation.AssetCorrelator;
import com.rapid7.nexpose.console.domain.Asset;
import com.rapid7.nexpose.console.domain.Vulnerability;
import com.rapid7.nexpose.console.exception.ScanEngineException;
import com.rapid7.nexpose.console.risk.RiskCalculator;
import com.rapid7.nexpose.console.scan.ScanEngine;
import com.rapid7.nexpose.console.scan.ScanEnginePool;
import com.rapid7.nexpose.console.scan.ScanTargetParser;
import com.rapid7.nexpose.console.util.DateUtils;
import com.rapid7.nexpose.nsc.repository.AssetRecord;
import com.rapid7.nexpose.nsc.repository.ScanRecord;
import com.rapid7.nexpose.nsc.repository.ScanRepository;
import com.rapid7.nexpose.nsc.repository.VulnerabilityRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import com.rapid7.nexpose.nsc.config.NexposeProperties;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Runs scans end-to-end: parse targets, run the engine through the pool, correlate
 * duplicates, score risk, and persist. Foreground scans run synchronously; the
 * "run in background" option submits to the (misconfigured) async executor.
 *
 * <p>Defect routing:
 * NEX-3103 (parser CIDR) via {@code parser.resolve}; NEX-3107 (pool leak) via
 * {@code pool.runScan} when the engine throws (sentinel 0.0.0.0);
 * NEX-3104 (CME) via {@code correlator.mergeDuplicates} on duplicate IPs;
 * NEX-3102 (divide by zero) via {@code riskCalculator} on the empty-vuln sentinel;
 * NEX-3110 (async executor) via {@code scanExecutor} on background scans.</p>
 */
@Service
public class ScanService {

    private static final Logger log = LoggerFactory.getLogger(ScanService.class);
    private static final DateTimeFormatter OFFSET_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");

    private final ScanTargetParser parser;
    private final ScanEnginePool pool;
    private final ScanEngine engine;
    private final AssetCorrelator correlator;
    private final RiskCalculator riskCalculator;
    private final ScanRepository scanRepository;
    private final NexposeProperties props;

    public ScanService(ScanTargetParser parser,
                       ScanEnginePool pool,
                       ScanEngine engine,
                       AssetCorrelator correlator,
                       RiskCalculator riskCalculator,
                       ScanRepository scanRepository,
                       NexposeProperties props) {
        this.parser = parser;
        this.pool = pool;
        this.engine = engine;
        this.correlator = correlator;
        this.riskCalculator = riskCalculator;
        this.scanRepository = scanRepository;
        this.props = props;
    }

    public ScanRecord runScan(String scanName, List<String> targets, boolean background) {
        if (background) {
            return submitBackground(scanName, targets);
        }
        return executeScan(scanName, targets);
    }

    /**
     * Background path — obtains the lazily-initialised async executor. When
     * {@code nexpose.scan.async.max-pool-size} is 0 (NEX-3110) this throws
     * {@code IllegalArgumentException} during executor initialisation.
     */
    private ScanRecord submitBackground(String scanName, List<String> targets) {
        log.info("Submitting background scan '{}' to async executor", scanName);
        // NEX-3110: max pool size of 0 (from nexpose.scan.async.max-pool-size) is
        // invalid; initialize() throws IllegalArgumentException here.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(props.getScan().getAsync().getCorePoolSize());
        executor.setMaxPoolSize(props.getScan().getAsync().getMaxPoolSize());
        executor.setQueueCapacity(props.getScan().getAsync().getQueueCapacity());
        executor.setThreadNamePrefix("scan-async-");
        log.info("Initialising background scan executor (core={}, max={}, queue={})",
                props.getScan().getAsync().getCorePoolSize(),
                props.getScan().getAsync().getMaxPoolSize(),
                props.getScan().getAsync().getQueueCapacity());
        executor.initialize();                                    // <-- NEX-3110
        executor.submit(() -> executeScan(scanName, targets));
        ScanRecord queued = new ScanRecord();
        queued.setName(scanName);
        queued.setEngineName(engine.name());
        queued.setStatus("PENDING");
        queued.setStartedAt(Instant.now());
        return scanRepository.save(queued);
    }

    private ScanRecord executeScan(String scanName, List<String> targets) {
        log.info("Starting scan '{}' with {} target expression(s): {}", scanName, targets.size(), targets);
        ScanRecord record = new ScanRecord();
        record.setName(scanName);
        record.setEngineName(engine.name());
        record.setStatus("RUNNING");
        record.setStartedAt(Instant.now());

        List<String> hosts = parser.resolve(targets);                 // NEX-3103
        List<Asset> assets = pool.runScan(engine, hosts);             // NEX-3107 (engine fault leaks slot)
        assets = correlator.mergeDuplicates(assets);                  // NEX-3104 (duplicate IPs)

        for (Asset asset : assets) {
            riskCalculator.scoreAsset(asset);                         // NEX-3102 (empty-vuln sentinel)
        }

        persistAssets(record, assets);
        record.setStatus("COMPLETED");
        record.setFinishedAt(Instant.now());
        // Raw engine completion stamp uses a numeric GMT offset; the Scan Detail
        // view parses this via DateUtils and triggers NEX-3106.
        record.setFailureReason(null);
        ScanRecord saved = scanRepository.save(record);
        log.info("Scan '{}' completed: {} asset(s), {} vulnerability record(s)",
                scanName, saved.getAssets().size(), countVulns(saved));
        return saved;
    }

    private void persistAssets(ScanRecord record, List<Asset> assets) {
        for (Asset a : assets) {
            AssetRecord ar = new AssetRecord();
            ar.setIpAddress(a.getIpAddress());
            ar.setHostName(a.getHostName());
            ar.setOperatingSystem(a.getOperatingSystem());
            ar.setLive(a.isLive());
            ar.setFingerprinted(a.getVulnerabilities() != null);
            ar.setRiskScore(a.getRiskScore());
            ar.setScan(record);
            if (a.getVulnerabilities() != null) {
                for (Vulnerability v : a.getVulnerabilities()) {
                    VulnerabilityRecord vr = new VulnerabilityRecord();
                    vr.setVulnId(v.getId());
                    vr.setTitle(v.getTitle());
                    vr.setCvssScore(v.getCvssScore());
                    vr.setSeverity(v.getSeverity() == null ? null : v.getSeverity().name());
                    vr.setCve(v.getCve());
                    vr.setPort(v.getPort());
                    vr.setProtocol(v.getProtocol());
                    vr.setAsset(ar);
                    ar.getVulnerabilities().add(vr);
                }
            }
            record.getAssets().add(ar);
        }
    }

    /** The ISO-8601 completion timestamp with a numeric offset (feeds NEX-3106). */
    public String rawCompletionStamp(ScanRecord record) {
        Instant when = record.getFinishedAt() != null ? record.getFinishedAt() : Instant.now();
        return OffsetDateTime.ofInstant(when, ZoneOffset.UTC).format(OFFSET_ISO);
    }

    private long countVulns(ScanRecord record) {
        long n = 0;
        for (AssetRecord a : record.getAssets()) {
            n += a.getVulnerabilities().size();
        }
        return n;
    }

    /**
     * Parse the raw engine completion stamp for the Scan Detail view. The stamp
     * carries a numeric GMT offset (e.g. +00:00) which the DateUtils pattern (a
     * literal 'Z') cannot parse -> NEX-3106.
     */
    public java.util.Date parseCompletion(ScanRecord record) {
        String raw = rawCompletionStamp(record);
        log.info("Rendering completion time for scan '{}' from raw stamp {}", record.getName(), raw);
        return DateUtils.parseScanTimestamp(raw);          // NEX-3106
    }

    public List<ScanRecord> recentScans() {
        return scanRepository.findAllByOrderByStartedAtDesc();
    }

    public ScanRecord getScan(Long id) {
        return scanRepository.findById(id)
                .orElseThrow(() -> new ScanEngineException("No scan with id " + id));
    }
}
