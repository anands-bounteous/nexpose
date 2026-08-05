package com.rapid7.nexpose.nsc.repository;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/** JPA entity for a persisted asset. */
@Entity
@Table(name = "asset_record")
public class AssetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;
    private String hostName;
    private String operatingSystem;
    private boolean live;
    private boolean fingerprinted = true;
    private double riskScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_id")
    private ScanRecord scan;

    @OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<VulnerabilityRecord> vulnerabilities = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }
    public boolean isLive() { return live; }
    public void setLive(boolean live) { this.live = live; }
    public boolean isFingerprinted() { return fingerprinted; }
    public void setFingerprinted(boolean fingerprinted) { this.fingerprinted = fingerprinted; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public ScanRecord getScan() { return scan; }
    public void setScan(ScanRecord scan) { this.scan = scan; }
    public Set<VulnerabilityRecord> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(Set<VulnerabilityRecord> v) { this.vulnerabilities = v; }
}
