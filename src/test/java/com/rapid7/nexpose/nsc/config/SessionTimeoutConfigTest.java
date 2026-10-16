package com.rapid7.nexpose.nsc.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Regression test for SI-68 / SI-3166: server.servlet.session.timeout was
 * shipped as 30 seconds, which logs interactive users out almost
 * immediately. This test asserts the shipped application.properties no
 * longer configures an impractically short idle session timeout.
 */
class SessionTimeoutConfigTest {

    @Test
    void sessionTimeoutIsNotAnImpracticallyShortDuration() throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            assertNotNull(in, "application.properties must be on the classpath");
            props.load(in);
        }

        String timeout = props.getProperty("server.servlet.session.timeout");
        assertNotNull(timeout, "server.servlet.session.timeout must be set");

        // Fails for values like "30s" (seconds-scale timeouts); passes for
        // minute/hour-scale values such as "30m".
        assertFalse(timeout.trim().endsWith("s"),
                "Session timeout should not be configured in seconds-scale "
                        + "(too short for interactive use): " + timeout);
    }
}