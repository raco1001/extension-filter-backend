package com.example.flow.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "user_extensions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "extension_id"})
})
public class UserExtension {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extension_id", nullable = false)
    private UserExtension extension;
    
    
    
}
