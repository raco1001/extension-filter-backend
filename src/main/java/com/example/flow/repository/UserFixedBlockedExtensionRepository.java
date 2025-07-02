package com.example.flow.repository;

import com.example.flow.domain.UserFixedBlockedExtension;
import com.example.flow.domain.User;
import com.example.flow.domain.FixedBlockedExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFixedBlockedExtensionRepository extends JpaRepository<UserFixedBlockedExtension, UUID> {
    List<UserFixedBlockedExtension> findByUser(User user);
    Optional<UserFixedBlockedExtension> findByUserAndExtension(User user, FixedBlockedExtension extension);
} 