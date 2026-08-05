package com.rapid7.nexpose.nsc.controller;

import com.rapid7.nexpose.nsc.repository.ScanRecord;
import com.rapid7.nexpose.nsc.service.ScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Scan configuration and execution.
 *
 * <p>The targets textarea accepts IPs, CIDRs and hostnames (one per line or comma
 * separated). Sentinels: a malformed CIDR triggers NEX-3103; {@code 0.0.0.0}
 * triggers an engine fault (and, on repeat, the NEX-3107 pool leak); listing the
 * same IP twice triggers NEX-3104; {@code 198.51.100.7} triggers NEX-3102;
 * "run in background" triggers NEX-3110.</p>
 */
@Controller
public class ScanController {

    private static final Logger log = LoggerFactory.getLogger(ScanController.class);

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @GetMapping("/scan")
    public String scanForm(Model model) {
        model.addAttribute("engineName", "Local scan engine");
        return "scan";
    }

    @PostMapping("/scan")
    public String runScan(@RequestParam String scanName,
                          @RequestParam String targets,
                          @RequestParam(defaultValue = "false") boolean background,
                          Model model) {
        List<String> targetList = parseTargets(targets);
        log.info("Received scan request '{}' with {} target line(s), background={}",
                scanName, targetList.size(), background);
        ScanRecord record = scanService.runScan(scanName, targetList, background);
        return "redirect:/scan/" + record.getId();
    }

    @GetMapping("/scan/{id}")
    public String scanDetail(@PathVariable Long id, Model model) {
        ScanRecord record = scanService.getScan(id);
        model.addAttribute("scan", record);
        // NEX-3106: parsing the raw engine completion stamp throws here.
        model.addAttribute("completedAt", scanService.parseCompletion(record));
        return "scan-detail";
    }

    @GetMapping("/scans")
    public String scanList(Model model) {
        model.addAttribute("scans", scanService.recentScans());
        return "scans";
    }

    private List<String> parseTargets(String raw) {
        List<String> targets = new ArrayList<>();
        if (raw == null) {
            return targets;
        }
        for (String token : raw.split("[,\\r\\n]+")) {
            String t = token.trim();
            if (!t.isEmpty()) {
                targets.add(t);
            }
        }
        return targets;
    }
}
