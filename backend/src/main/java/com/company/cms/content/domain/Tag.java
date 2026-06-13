package com.company.cms.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "normalized_name", nullable = false, unique = true)
    private String normalizedName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Tag() {
    }

    public Tag(String name) {
        this.name = name;
        this.normalizedName = normalize(name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
