package com.rapid7.nexpose.nsc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserRecord, Long> {
    Optional<UserRecord> findByUsernameIgnoreCase(String username);
}
