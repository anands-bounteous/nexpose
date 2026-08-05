package com.rapid7.nexpose.nsc.repository;

import jakarta.persistence.*;

/** JPA entity for a console user. */
@Entity
@Table(name = "user_record")
public class UserRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String displayName;
    private String passwordHash;
    private boolean ldap;
    private boolean enabled = true;
    private String roles;   // comma-separated

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public boolean isLdap() { return ldap; }
    public void setLdap(boolean ldap) { this.ldap = ldap; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
}
