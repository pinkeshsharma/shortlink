package com.shortlink.service;

import com.shortlink.db.entity.ShortLink;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShortLinkService {
    @Transactional
    String createOrGetShortCode(String originalUrl,
                                String customCode,
                                String tenantId,
                                String domain,
                                long expiresAt);

    @Cacheable(value = "shortlinks", key = "#shortCode + '_' + #tenantId")
    ShortLink getByShortCode(String shortCode, String tenantId);

    Page<ShortLink> getAllLinks(String tenantId, Pageable pageable);
}
