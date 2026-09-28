package com.rapid7.nexpose.nsc.controller;

import com.rapid7.nexpose.nsc.repository.AssetRecord;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Regression test for SI-3150: the asset-detail JSP inverted the Live status
 * ("${!asset.live ? 'yes' : 'no'}"), causing a live asset to display
 * "Live: no" and vice versa. This test verifies the JSP source no longer
 * contains the inverted expression.
 */
class AssetDetailLiveRenderingTest {

    @Test
    void assetDetailJspDoesNotInvertLiveFlag() throws Exception {
        Path jsp = Path.of("src/main/webapp/WEB-INF/jsp/asset-detail.jsp");
        String content = Files.readString(jsp);
        assertFalse(content.contains("${!asset.live ? 'yes' : 'no'}"),
                "asset-detail.jsp must not invert the live flag");
        assertTrue(content.contains("${asset.live ? 'yes' : 'no'}"),
                "asset-detail.jsp must render the live flag directly");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
