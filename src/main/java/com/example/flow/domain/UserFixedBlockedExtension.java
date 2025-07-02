package com.example.flow.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "user_fixed_blocked_extensions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "extension_id"})
})
public class UserFixedBlockedExtension {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extension_id", nullable = false)
    private FixedBlockedExtension extension;

    @Column(nullable = false)
    private boolean isBlocked;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void setId(UUID id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setExtension(FixedBlockedExtension extension) { this.extension = extension; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }
} 