package com.rapid7.nexpose.nsc.controller;

import com.rapid7.nexpose.nsc.repository.ReportHistoryDao;
import com.rapid7.nexpose.nsc.repository.ScanRecord;
import com.rapid7.nexpose.nsc.repository.ScanRepository;
import com.rapid7.nexpose.nsc.service.ReportService;
import com.rapid7.nexpose.nsc.service.ScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Report generation.
 *
 * <p>{@code /report/xml} generates a plain XML report (NEX-3101 for scans with an
 * unfingerprinted asset). {@code /report/preview} exercises the sectioned report
 * engine (NEX-3105). {@code /report/history} lists prior reports (NEX-3109).</p>
 */
@Controller
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;
    private final ScanService scanService;
    private final ReportHistoryDao reportHistoryDao;
    private final ScanRepository scanRepository;

    public ReportController(ReportService reportService, ScanService scanService,
                            ReportHistoryDao reportHistoryDao, ScanRepository scanRepository) {
        this.reportService = reportService;
        this.scanService = scanService;
        this.reportHistoryDao = reportHistoryDao;
        this.scanRepository = scanRepository;
    }

    @GetMapping("/report")
    public String reportForm(Model model) {
        model.addAttribute("scans", scanService.recentScans());
        return "report";
    }

    @GetMapping("/report/xml")
    @ResponseBody
    public ResponseEntity<String> generateXml(@RequestParam Long scanId) {
        ScanRecord record = scanService.getScan(scanId);
        log.info("Generating XML report for scan '{}' ({} vulnerabilit(y/ies) on record)",
                record.getName(), scanRepository.countVulnerabilities(scanId));   // SI-3144
        String xml = reportService.generateXml(record);        // NEX-3101
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"scan-" + scanId + "-report.xml\"");
        return new ResponseEntity<>(xml, headers, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/report/preview")
    public String preview(@RequestParam Long scanId, Model model) {
        ScanRecord record = scanService.getScan(scanId);
        model.addAttribute("xml", reportService.previewLayout(record));   // NEX-3105
        model.addAttribute("scan", record);
        return "report-preview";
    }

    @GetMapping("/report/history")
    public String history(Model model) {
        model.addAttribute("history", reportHistoryDao.recent(50));       // NEX-3109
        return "report-history";
    }
}
