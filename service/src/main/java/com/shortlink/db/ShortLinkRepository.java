package com.shortlink.db;

import com.shortlink.db.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLink, String> {
    Optional<ShortLink> findByShortCodeAndTenantId(String shortCode, String tenantId);

    Optional<ShortLink> findByOriginalUrlAndTenantId(String url, String tenantId);
}
