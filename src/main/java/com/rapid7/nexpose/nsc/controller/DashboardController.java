package com.rapid7.nexpose.nsc.controller;

import com.rapid7.nexpose.nsc.service.DashboardService;
import com.rapid7.nexpose.nsc.service.ScanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Landing dashboard with headline metrics. */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final ScanService scanService;

    public DashboardController(DashboardService dashboardService, ScanService scanService) {
        this.dashboardService = dashboardService;
        this.scanService = scanService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.summary());
        model.addAttribute("recentScans", scanService.recentScans());
        return "dashboard";
    }
}
