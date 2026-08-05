package com.rapid7.nexpose.nsc.service;

import com.rapid7.nexpose.console.domain.Asset;
import com.rapid7.nexpose.console.domain.Scan;
import com.rapid7.nexpose.console.domain.ScanStatus;
import com.rapid7.nexpose.console.domain.Severity;
import com.rapid7.nexpose.console.domain.Vulnerability;
import com.rapid7.nexpose.console.report.ReportEngine;
import com.rapid7.nexpose.console.report.XmlReportGenerator;
import com.rapid7.nexpose.nsc.repository.AssetRecord;
import com.rapid7.nexpose.nsc.repository.ScanRecord;
import com.rapid7.nexpose.nsc.repository.VulnerabilityRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds reports from persisted scans.
 *
 * <p>Defect routing:
 * {@link #generateXml(ScanRecord)} calls {@link XmlReportGenerator} directly and
 * throws NEX-3101 (NullPointerException) for scans containing an unfingerprinted
 * asset (null vulnerability list). {@link #previewLayout(ScanRecord)} exercises the
 * {@link ReportEngine} section assembly and throws NEX-3105 (ClassCastException).</p>
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final XmlReportGenerator xmlReportGenerator;
    private final ReportEngine reportEngine;

    public ReportService(XmlReportGenerator xmlReportGenerator, ReportEngine reportEngine) {
        this.xmlReportGenerator = xmlReportGenerator;
        this.reportEngine = reportEngine;
    }

    /** Plain XML report (NEX-3101 path). */
    public String generateXml(ScanRecord record) {
        log.info("Generating XML report for scan '{}' (id={})", record.getName(), record.getId());
        Scan scan = toDomain(record);
        return xmlReportGenerator.generate(scan);          // NEX-3101 on null vuln list
    }

    /** Sectioned report assembly (NEX-3105 path). */
    public String previewLayout(ScanRecord record) {
        log.info("Assembling sectioned report layout for scan '{}'", record.getName());
        Scan scan = toDomain(record);
        return reportEngine.generateXml(scan);             // NEX-3105 ClassCastException
    }

    private Scan toDomain(ScanRecord record) {
        Scan scan = new Scan();
        scan.setId(record.getId());
        scan.setName(record.getName());
        scan.setEngineName(record.getEngineName());
        scan.setStatus(safeStatus(record.getStatus()));
        for (AssetRecord ar : record.getAssets()) {
            Asset a = new Asset(ar.getIpAddress(), ar.getHostName(), ar.getOperatingSystem());
            a.setId(ar.getId());
            a.setLive(ar.isLive());
            a.setRiskScore(ar.getRiskScore());
            if (!ar.isFingerprinted()) {
                // Restore the unfingerprinted shape: null vulnerability list.
                a.setVulnerabilities(null);
            } else {
                List<Vulnerability> vulns = new ArrayList<>();
                for (VulnerabilityRecord vr : ar.getVulnerabilities()) {
                    Vulnerability v = new Vulnerability(
                            vr.getVulnId(), vr.getTitle(), vr.getCvssScore(), vr.getPort(), vr.getProtocol());
                    v.setCve(vr.getCve());
                    if (vr.getSeverity() != null) {
                        v.setSeverity(Severity.valueOf(vr.getSeverity()));
                    }
                    vulns.add(v);
                }
                a.setVulnerabilities(vulns);
            }
            scan.getAssets().add(a);
        }
        return scan;
    }

    private ScanStatus safeStatus(String s) {
        try {
            return s == null ? ScanStatus.COMPLETED : ScanStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            return ScanStatus.COMPLETED;
        }
    }
}
