package com.example.flow.repository;

import com.example.flow.domain.User;
import com.example.flow.domain.UserExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserExtensionsRepository extends JpaRepository<UserExtension, UUID> {
    List<UserExtension> findByUser(User user);
    Optional<UserExtension> findByUserAndExtension(User user, UserExtension extension);
}
