package com.rapid7.nexpose.nsc.config;

import com.rapid7.nexpose.nsc.repository.UserRecord;
import com.rapid7.nexpose.nsc.repository.UserRepository;
import com.rapid7.nexpose.nsc.service.ScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

/**
 * Seeds baseline data so the console is navigable on first launch: the built-in
 * {@code nxadmin} user and a small demo scan (clean, sentinel-free targets) that
 * populates the dashboard/asset/report views.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ScanService scanService;

    public DataInitializer(UserRepository userRepository, ScanService scanService) {
        this.userRepository = userRepository;
        this.scanService = scanService;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        seedDemoScan();
    }

    private void seedAdmin() {
        if (userRepository.findByUsernameIgnoreCase("nxadmin").isEmpty()) {
            UserRecord admin = new UserRecord();
            admin.setUsername("nxadmin");
            admin.setDisplayName("Nexpose Administrator");
            admin.setPasswordHash(sha256("nxadmin"));
            admin.setLdap(false);
            admin.setEnabled(true);
            admin.setRoles("global-admin");
            userRepository.save(admin);
            log.info("Seeded built-in administrator 'nxadmin'");
        }
    }

    private void seedDemoScan() {
        try {
            List<String> targets = List.of(
                    "10.0.0.1", "10.0.0.2", "10.0.0.3", "10.0.0.4",
                    "10.0.0.5", "10.0.0.6", "10.0.0.7", "10.0.0.8");
            scanService.runScan("Demo - Corporate /29", targets, false);
            log.info("Seeded demo scan with {} target(s)", targets.size());
        } catch (RuntimeException e) {
            // Never block startup on demo data.
            log.warn("Demo scan seeding skipped: {}", e.getMessage());
        }
    }

    private static String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
