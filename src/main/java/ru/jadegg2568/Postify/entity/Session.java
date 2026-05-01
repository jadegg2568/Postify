package ru.jadegg2568.Postify.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @UuidGenerator
    private UUID uuid;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private boolean cancelled = false;

    @Timestamp
    private Instant expiresAt;

    @CreationTimestamp
    private Instant createdAt;
}