package com.rapid7.nexpose.nse;

import com.rapid7.nexpose.console.domain.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** A unit of scan-engine work handed to the NSE. Mirrors nse/ScanJob.java. */
public class ScanJob implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ScanJob.class);

    private final NSE nse;
    private final List<String> hosts;
    private volatile List<Asset> result;

    public ScanJob(NSE nse, List<String> hosts) {
        this.nse = nse;
        this.hosts = hosts;
    }

    @Override
    public void run() {
        log.info("ScanJob running against {} host(s)", hosts.size());
        this.result = nse.engine().scan(hosts);
        log.info("ScanJob complete: {} asset(s)", result == null ? 0 : result.size());
    }

    public List<Asset> getResult() {
        return result;
    }
}
