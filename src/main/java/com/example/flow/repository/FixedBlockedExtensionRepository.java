package com.example.flow.repository;

import com.example.flow.domain.FixedBlockedExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FixedBlockedExtensionRepository extends JpaRepository<FixedBlockedExtension, UUID> {
    Optional<FixedBlockedExtension> findByExtension(String extension);
} 