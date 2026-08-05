package com.rapid7.nexpose.nsc.repository;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/** JPA entity for a persisted scan and its assets. */
@Entity
@Table(name = "scan_record")
public class ScanRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String engineName;
    private String status;
    private Instant startedAt;
    private Instant finishedAt;
    private String failureReason;

    @OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AssetRecord> assets = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEngineName() { return engineName; }
    public void setEngineName(String engineName) { this.engineName = engineName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Set<AssetRecord> getAssets() { return assets; }
    public void setAssets(Set<AssetRecord> assets) { this.assets = assets; }
}
