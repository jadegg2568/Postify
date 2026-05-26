package ru.jadegg2568.Postify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UuidGenerator
    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Email
    @Column(unique = true)
    private String mail;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false, length = 24)
    private String name;

    @Column(length = 32)
    private String displayName;

    @Column(length = 256)
    private String description;

    // S3 image key
    private String avatarKey;

    @Enumerated(EnumType.ORDINAL) // numeric type from id
    @Column(columnDefinition = "SMALLINT", nullable = false)
    private Permissions permissions;

    @CreationTimestamp
    private Instant createdAt;
}
