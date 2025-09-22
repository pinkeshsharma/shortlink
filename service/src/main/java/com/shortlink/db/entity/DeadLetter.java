package com.shortlink.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dlq")
@AllArgsConstructor
public class DeadLetter {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalUrl;
    private String reason;
    private String domain;
    private String customCode;
    private String tenantId;
    private Instant createdAt = Instant.now();

    public DeadLetter() {}

    public DeadLetter(String originalUrl, String domain, String customCode, String tenantId, String reason) {
        this.originalUrl = originalUrl;
        this.reason = reason;
        this.domain = domain;
        this.customCode = customCode;
        this.tenantId = tenantId;
    }
}
