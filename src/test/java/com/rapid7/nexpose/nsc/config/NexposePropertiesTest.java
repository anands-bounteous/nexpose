package com.rapid7.nexpose.nsc.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NexposePropertiesTest {
    @Test
    void retryOnFailureDefaultsToTrue() {
        assertTrue(new NexposeProperties().getScan().getAsync().isRetryOnFailure());
    }
}
