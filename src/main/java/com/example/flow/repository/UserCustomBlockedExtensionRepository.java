package com.example.flow.repository;

import com.example.flow.domain.UserCustomBlockedExtension;
import com.example.flow.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCustomBlockedExtensionRepository extends JpaRepository<UserCustomBlockedExtension, UUID> {
    List<UserCustomBlockedExtension> findByUser(User user);
    Optional<UserCustomBlockedExtension> findByUserAndExtension(User user, String extension);
    long countByUser(User user);
} 