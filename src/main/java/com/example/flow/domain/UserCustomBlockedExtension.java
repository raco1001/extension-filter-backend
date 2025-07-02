package com.example.flow.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "user_custom_blocked_extensions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "extension"})
})
public class UserCustomBlockedExtension {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String extension;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public void setId(UUID id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setExtension(String extension) { this.extension = extension; }
} 