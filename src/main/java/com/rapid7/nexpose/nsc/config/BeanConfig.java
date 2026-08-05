package com.rapid7.nexpose.nsc.config;

import com.rapid7.nexpose.console.auth.LdapAuthenticator;
import com.rapid7.nexpose.console.auth.LdapSettings;
import com.rapid7.nexpose.console.auth.LocalAuthenticator;
import com.rapid7.nexpose.console.scan.MockScanEngine;
import com.rapid7.nexpose.console.scan.ScanEngine;
import com.rapid7.nexpose.console.scan.ScanEnginePool;
import com.rapid7.nexpose.console.scan.ScanTargetParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the shared-library collaborators (which are plain classes, not
 * {@code @Component}s) into the Spring context, and builds the background scan
 * executor.
 */
@Configuration
@EnableConfigurationProperties(NexposeProperties.class)
public class BeanConfig {

    private static final Logger log = LoggerFactory.getLogger(BeanConfig.class);

    private final NexposeProperties props;

    public BeanConfig(NexposeProperties props) {
        this.props = props;
    }

    @Bean
    public LocalAuthenticator localAuthenticator() {
        return new LocalAuthenticator();   // seeds nxadmin/nxadmin
    }

    @Bean
    public LdapSettings ldapSettings() {
        // Values come straight from application.properties (nexpose.ldap.*),
        // which are intentionally wrong for the lab (NEX-3108).
        LdapSettings s = new LdapSettings();
        s.setEnabled(props.getLdap().isEnabled());
        s.setUrl(props.getLdap().getUrl());
        s.setBaseDn(props.getLdap().getBaseDn());
        s.setUserDnPattern(props.getLdap().getUserDnPattern());
        s.setConnectTimeoutMs(props.getLdap().getConnectTimeoutMs());
        log.info("LDAP configured: enabled={} url={} baseDn={}",
                s.isEnabled(), s.getUrl(), s.getBaseDn());
        return s;
    }

    @Bean
    public LdapAuthenticator ldapAuthenticator(LdapSettings ldapSettings) {
        return new LdapAuthenticator(ldapSettings);
    }

    @Bean
    public ScanEngine mockScanEngine() {
        return new MockScanEngine("Local scan engine");
    }

    @Bean
    public ScanTargetParser scanTargetParser() {
        return new ScanTargetParser();
    }

    @Bean
    public ScanEnginePool scanEnginePool() {
        // NEX-3107 lives inside runScan(); pool size itself comes from config.
        return new ScanEnginePool(
                props.getScan().getPoolSize(),
                props.getScan().getAcquireTimeoutSeconds());
    }

}
