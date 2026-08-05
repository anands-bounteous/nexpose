package com.rapid7.nexpose.nsc.controller;

import com.rapid7.nexpose.nsc.service.AssetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Browse assets and their vulnerabilities. */
@Controller
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/assets")
    public String assets(Model model) {
        model.addAttribute("assets", assetService.allAssets());
        return "assets";
    }

    @GetMapping("/assets/{id}")
    public String asset(@PathVariable Long id, Model model) {
        model.addAttribute("asset", assetService.getAsset(id));
        return "asset-detail";
    }
}
