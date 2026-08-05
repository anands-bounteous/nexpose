package com.rapid7.nexpose;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Entry point for the Nexpose Security Console web application (POC).
 *
 * <p>Component scanning covers both this application ({@code com.rapid7.nexpose})
 * and the shared library ({@code com.rapid7.nexpose.console}) so the library's
 * {@code @Component}s (RiskCalculator, AssetCorrelator, ReportEngine, ...) are
 * picked up.</p>
 */
@SpringBootApplication(scanBasePackages = "com.rapid7.nexpose")
public class NexposeConsoleApplication extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(NexposeConsoleApplication.class);

    public static void main(String[] args) {
        log.info("Bootstrapping Nexpose Security Console ...");
        SpringApplication.run(NexposeConsoleApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NexposeConsoleApplication.class);
    }
}
