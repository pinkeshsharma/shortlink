package com.shortlink.service;

import com.shortlink.db.DeadLetterRepository;
import com.shortlink.db.ShortLinkRepository;
import com.shortlink.db.entity.DeadLetter;
import com.shortlink.db.entity.ShortLink;
import jakarta.transaction.Transactional;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    private final ShortLinkRepository repository;
    private final DeadLetterRepository dlqRepo;
    private final CacheManager cacheManager;

    public ShortLinkServiceImpl(ShortLinkRepository repository,
                                DeadLetterRepository dlqRepo,
                                CacheManager cacheManager) {
        this.repository = repository;
        this.dlqRepo = dlqRepo;
        this.cacheManager = cacheManager;
    }

    @Transactional
    @Override
    public String createOrGetShortCode(String originalUrl,
                                       String customCode,
                                       String tenantId,
                                       String domain,
                                       long expiresAt) {
        String normalizedUrl;
        try {
            normalizedUrl = normalize(originalUrl);
        } catch (Exception e) {
            dlqRepo.save(new DeadLetter(originalUrl, domain, customCode, tenantId, "Invalid URL", expiresAt));
            throw new IllegalArgumentException("Invalid URL");
        }

        Optional<ShortLink> existing = repository.findByOriginalUrlAndTenantId(normalizedUrl, tenantId);
        if (existing.isPresent() && !existing.get().isExpired()) {
            return existing.get().getShortCode();
        }

        String shortCode = (customCode != null && !customCode.isBlank())
                ? customCode
                : UUID.randomUUID().toString().substring(0, 8);

        ShortLink entity = new ShortLink();
        entity.setOriginalUrl(normalizedUrl);
        entity.setTenantId(tenantId);
        entity.setDomain(domain);
        entity.setShortCode(shortCode);

        if (expiresAt > 0) {
            entity.setExpiresAt(expiresAt);
        } else {
            // TTL 30 days
            entity.setExpiresAt(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000));
        }

        repository.save(entity);

        // Warm cache
        cacheManager.getCache("shortlinks").put(shortCode + "_" + tenantId, entity);

        return shortCode;
    }

    @Override
    @Cacheable(value = "shortlinks", key = "#shortCode + '_' + #tenantId", unless = "#result == null")
    public ShortLink getByShortCode(String shortCode, String tenantId) {
        ShortLink link = repository.findByShortCodeAndTenantId(shortCode, tenantId)
                .orElseThrow(() -> new RuntimeException("Short link not found"));

        if (link.isExpired()) {
            throw new RuntimeException("Short link expired");
        }

        return link;
    }

    @Override
    public Page<ShortLink> getAllLinks(String tenantId, Pageable pageable) {
        return repository.findByTenantId(tenantId, pageable);
    }

    private String normalize(String url) throws URISyntaxException {
        URI uri = new URI(url);
        if (uri.getScheme() == null) {
            uri = new URI("http://" + url);
        }
        return uri.toString();
    }
}
