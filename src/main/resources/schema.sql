-- Report history table.
--
-- DEFECT NEX-3109: this script only runs when spring.sql.init.mode is enabled
-- (e.g. 'always'). The shipped application.properties sets it to 'never', so this
-- table is never created and ReportHistoryDao fails with
--   org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "REPORT_HISTORY" not found
-- FIX: set spring.sql.init.mode=always.
CREATE TABLE IF NOT EXISTS report_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    scan_name   VARCHAR(255),
    format      VARCHAR(32),
    size_bytes  INT,
    created_at  TIMESTAMP
);
