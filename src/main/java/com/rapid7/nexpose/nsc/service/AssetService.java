package com.rapid7.nexpose.nsc.service;

import com.rapid7.nexpose.console.exception.AssetNotFoundException;
import com.rapid7.nexpose.nsc.repository.AssetRecord;
import com.rapid7.nexpose.nsc.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/** Read access to persisted assets and their vulnerabilities. */
@Service
public class AssetService {

    private static final Logger log = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    public List<AssetRecord> allAssets() {
        // BUG (SI-3148): the asset list page's UI copy claims "oldest first",
        // but this returns newest-discovered (highest id) first.
        return assetRepository.findAllByOrderByIdDesc();
    }

    public AssetRecord getAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new AssetNotFoundException("No asset with id " + id));
    }
}
