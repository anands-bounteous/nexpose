package com.rapid7.nexpose.nse;

import com.rapid7.nexpose.console.scan.MockScanEngine;
import com.rapid7.nexpose.console.scan.ScanEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal stand-in for the Nexpose Scan Engine (NSE) process. In the real product
 * the NSE is a separate service that the console (NSC) drives over a socket; here
 * it simply adapts the in-process {@link MockScanEngine} so the package structure
 * mirrors {@code src/nexpose/nse}.
 */
public class NSE {

    private static final Logger log = LoggerFactory.getLogger(NSE.class);

    private final ScanEngine engine;

    public NSE(String name) {
        this.engine = new MockScanEngine(name);
        log.info("NSE '{}' initialised", name);
    }

    public ScanEngine engine() {
        return engine;
    }
}
