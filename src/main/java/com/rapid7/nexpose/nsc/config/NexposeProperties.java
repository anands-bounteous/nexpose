package com.rapid7.nexpose.nsc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for {@code nexpose.*} settings in application.properties.
 * Several intentional configuration defects live in these values:
 * <ul>
 *   <li>NEX-3108 — {@code nexpose.ldap.url} / {@code nexpose.ldap.base-dn} point at
 *       a non-existent directory.</li>
 *   <li>NEX-3110 — {@code nexpose.scan.async.max-pool-size} is 0, so the background
 *       scan executor cannot be initialised.</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "nexpose")
public class NexposeProperties {

    private final Scan scan = new Scan();
    private final Ldap ldap = new Ldap();

    public Scan getScan() { return scan; }
    public Ldap getLdap() { return ldap; }

    public static class Scan {
        private int poolSize = 3;
        private long acquireTimeoutSeconds = 5;
        private final Async async = new Async();

        public int getPoolSize() { return poolSize; }
        public void setPoolSize(int poolSize) { this.poolSize = poolSize; }
        public long getAcquireTimeoutSeconds() { return acquireTimeoutSeconds; }
        public void setAcquireTimeoutSeconds(long v) { this.acquireTimeoutSeconds = v; }
        public Async getAsync() { return async; }

        public static class Async {
            private int corePoolSize = 1;
            private int maxPoolSize = 4;
            private int queueCapacity = 25;

            /**
             * Whether a background scan job that fails should be retried once.
             *
             * <p>BUG (SI-3146): background scan jobs are documented (README, ops
             * runbook) as retried-by-default, so this should default to
             * {@code true}. The Java-side default here is {@code false}, and
             * nothing in {@code application.properties} overrides it.</p>
             */
            private boolean retryOnFailure = false;

            public int getCorePoolSize() { return corePoolSize; }
            public void setCorePoolSize(int corePoolSize) { this.corePoolSize = corePoolSize; }
            public int getMaxPoolSize() { return maxPoolSize; }
            public void setMaxPoolSize(int maxPoolSize) { this.maxPoolSize = maxPoolSize; }
            public int getQueueCapacity() { return queueCapacity; }
            public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
            public boolean isRetryOnFailure() { return retryOnFailure; }
            public void setRetryOnFailure(boolean retryOnFailure) { this.retryOnFailure = retryOnFailure; }
        }
    }

    public static class Ldap {
        private boolean enabled = true;
        private String url = "ldap://ldap.lab.rapid7.com:389";
        private String baseDn = "dc=rapid7,dc=com";
        private String userDnPattern = "uid={0},ou=people";
        private int connectTimeoutMs = 5000;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getBaseDn() { return baseDn; }
        public void setBaseDn(String baseDn) { this.baseDn = baseDn; }
        public String getUserDnPattern() { return userDnPattern; }
        public void setUserDnPattern(String p) { this.userDnPattern = p; }
        public int getConnectTimeoutMs() { return connectTimeoutMs; }
        public void setConnectTimeoutMs(int v) { this.connectTimeoutMs = v; }
    }
}
