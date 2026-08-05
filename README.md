# nexpose-root

The runnable **Nexpose Security Console** web application (Spring Boot 3, Java 17,
JSP UI). It depends on the `nexpose-console` shared library and mirrors the
`rapid7/nexpose` layout (`com.rapid7.nexpose.nsc.*`, plus a small `nse` package).

## Build & run

`nexpose-console` must be installed to the local Maven repo **first**, then build
and run this app:

```bash
# 1) install the shared library
cd nexpose-console
mvn clean install

# 2) build and run the web app
cd ../nexpose-root
mvn clean package
java -jar target/nexpose-root-1.0.0.war
#   ... or, for iterative dev:
mvn spring-boot:run
```

Then open <http://localhost:8090/> and sign in.

- **Default login:** `nxadmin` / `nxadmin`
- Any other username is treated as a **directory (LDAP)** user and goes through
  the LDAP path (which is intentionally misconfigured — see NEX-3108).
- H2 console: <http://localhost:8090/h2-console> (JDBC URL `jdbc:h2:mem:nexpose`).
- Application log file: `logs/nexpose-console.log` (format matches the defect
  analysis pipeline exactly).

> **Note on this build environment:** the packaging sandbox has only a JRE (no
> `javac`/Maven) and no access to Maven Central, so the projects were authored but
> **not compiled here**. Build them on a machine with JDK 17 + Maven and internet
> access using the commands above.

## Features

- **Scan** — enter IPs / CIDR ranges / hostnames; a mock scan engine fingerprints
  hosts and fabricates vulnerabilities. Foreground (synchronous) or background.
- **Assets & vulnerabilities** — browse discovered assets and their findings.
- **XML report generation** — per-scan XML report listing assets + vulnerabilities.
- **Risk scoring & correlation** — CVSS-weighted risk, duplicate-asset correlation,
  domain roll-up.
- **Authentication** — built-in local admin plus JNDI/LDAP directory login.
- **H2 persistence** — scans, assets, vulnerabilities, users, report history.
- **Custom exceptions + global handler** — every failure renders `error.jsp` with a
  stable error code and the stack trace, and is logged in the pipeline's format.

## The 10 planted defects (all reachable from the UI)

| ID | Type | Trigger (how to reproduce in the UI) |
|----|------|--------------------------------------|
| NEX-3101 | code | Generate an XML report for a scan that contains an *unfingerprinted* asset (a normal scan of several hosts almost always includes one). `Reports → Download XML`. |
| NEX-3102 | code | Run a scan that includes the sentinel target `198.51.100.7` (fingerprinted, 0 vulns) → divide-by-zero while risk scoring. |
| NEX-3103 | code | Run a scan with a malformed CIDR target, e.g. `10.0.0.0/2a` or `10.0.0.0/24 ` → `NumberFormatException`. |
| NEX-3104 | code | Run a scan listing the same IP twice (e.g. `10.0.0.5` on two lines) → `ConcurrentModificationException` during correlation. |
| NEX-3105 | code | `Reports → Preview sectioned layout` → `ClassCastException` in the report engine. |
| NEX-3106 | code | Open any completed scan's detail page (`/scan/{id}`) → timestamp parse failure. |
| NEX-3107 | code | Run a scan of `0.0.0.0` (engine fault) `pool-size + 1` times → the pool leaks slots and finally throws `ScanEnginePoolExhaustedException`. |
| NEX-3108 | **config** | Log in with any non-`nxadmin` username (a directory user) → LDAP bind fails because `nexpose.ldap.url` / `base-dn` are wrong. |
| NEX-3109 | **config** | Open `Report History` → `Table "REPORT_HISTORY" not found`, because `spring.sql.init.mode=never` skips `schema.sql`. |
| NEX-3110 | **config** | Run a scan with **Run in background** checked → the async executor can't initialise because `nexpose.scan.async.max-pool-size=0`. |

Each defect has a matching Jira ticket and a ≥1000-line log fixture in the
`test-data` archive, with stack traces that reference these exact classes/methods.
Fixes are noted inline in the source (code defects) and in `application.properties`
(config defects).
