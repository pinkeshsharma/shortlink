package com.shortlink.service;

import com.shortlink.db.entity.ShortLink;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ShortLinkService {
    @Transactional
    String createOrGetShortCode(String originalUrl,
                                String customCode,
                                String tenantId,
                                String domain);

    @Cacheable(value = "shortlinks", key = "#shortCode + '_' + #tenantId")
    ShortLink getByShortCode(String shortCode, String tenantId);

    Page<ShortLink> getAllLinks(Pageable pageable);
}
