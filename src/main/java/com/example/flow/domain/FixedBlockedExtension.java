package com.example.flow.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fixed_blocked_extensions")
public class FixedBlockedExtension {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String extension;

    @Column(length = 50)
    private String displayName;

    @Column(nullable = false)
    private boolean isBlocked;

    @Column(nullable = false)
    private boolean isSystem;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 