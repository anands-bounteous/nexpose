package com.rapid7.nexpose.nsc.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Records and lists report-generation history in the {@code report_history}
 * table.
 *
 * <p><b>Known defect NEX-3109 (configuration issue):</b> the {@code report_history}
 * table is created by {@code src/main/resources/schema.sql}, which Spring Boot only
 * runs when SQL initialization is enabled. The shipped {@code application.properties}
 * sets {@code spring.sql.init.mode=never}, so the script never runs and the table
 * is never created. Every call here fails with
 * {@code org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "REPORT_HISTORY" not found}.
 * The JPA entity tables (scan/asset/vulnerability/user) are created by
 * {@code ddl-auto=update} and are unaffected — only report history breaks, which
 * is what makes this a narrowly-scoped configuration defect. The fix is to set
 * {@code spring.sql.init.mode=always} (no code change).</p>
 */
@Repository
public class ReportHistoryDao {

    private static final Logger log = LoggerFactory.getLogger(ReportHistoryDao.class);

    private final JdbcTemplate jdbc;

    public ReportHistoryDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void record(String scanName, String format, int sizeBytes) {
        log.info("Recording report history: scan='{}' format={} size={}B", scanName, format, sizeBytes);
        // Fails: table REPORT_HISTORY not found when spring.sql.init.mode=never.
        jdbc.update(
                "INSERT INTO report_history (scan_name, format, size_bytes, created_at) VALUES (?, ?, ?, ?)",
                scanName, format, sizeBytes, Timestamp.from(Instant.now()));
    }

    public List<Map<String, Object>> recent(int limit) {
        log.debug("Loading up to {} report-history row(s)", limit);
        return jdbc.queryForList(
                "SELECT scan_name, format, size_bytes, created_at " +
                "FROM report_history ORDER BY created_at DESC LIMIT ?", limit);
    }
}
