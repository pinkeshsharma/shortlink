package com.shortlink.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "short_links")
public class ShortLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String shortCode;

    @Column(nullable = false)
    private long createdAt = System.currentTimeMillis();

    @Column
    private long expiresAt;

    @Transient
    @JsonIgnore
    public boolean isExpired() {
        return expiresAt > 0 && expiresAt < System.currentTimeMillis();
    }

}
