package com.shortlink.service;

import com.shortlink.db.DeadLetterRepository;
import com.shortlink.db.ShortLinkRepository;
import com.shortlink.db.entity.ShortLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShortLinkServiceImplTest {

    private ShortLinkRepository shortLinkRepository;
    private DeadLetterRepository deadLetterRepository;
    private CacheManager cacheManager;
    private ShortLinkServiceImpl service;

    @BeforeEach
    void setUp() {
        shortLinkRepository = mock(ShortLinkRepository.class);
        deadLetterRepository = mock(DeadLetterRepository.class);
        cacheManager = mock(CacheManager.class);

        Cache cache = mock(Cache.class);
        // Make sure both "shortLinks" and "shortlinks" return the same mock
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        service = new ShortLinkServiceImpl(shortLinkRepository, deadLetterRepository, cacheManager);
    }


    @Test
    void testNormalizeAddsHttpWhenMissing() {
        when(shortLinkRepository.findByOriginalUrlAndTenantId(any(), any()))
                .thenReturn(Optional.empty());

        String code = service.createOrGetShortCode("example.com/page", null, "tenant1", "short.ly");
        assertNotNull(code);
        verify(shortLinkRepository).save(argThat(sl -> sl.getOriginalUrl().startsWith("http://")));
    }

    @Test
    void testNormalizePreservesHttps() {
        when(shortLinkRepository.findByOriginalUrlAndTenantId(any(), any()))
                .thenReturn(Optional.empty());

        String code = service.createOrGetShortCode("https://secure.com", null, "tenant1", "short.ly");
        assertNotNull(code);
        verify(shortLinkRepository).save(argThat(sl -> sl.getOriginalUrl().equals("https://secure.com")));
    }

    @Test
    void testInvalidUrlGoesToDeadLetterQueue() {
        assertThrows(IllegalArgumentException.class, () ->
                service.createOrGetShortCode(":::::/invalid", null, "tenant1", "short.ly")
        );
        verify(deadLetterRepository).save(any());
    }

    @Test
    void testIdempotentShortLinkCreation() {
        String tenantId = "tenant1";
        String url = "http://example.com";
        String domain = "http://short.ly";
        String existingCode = "abc123";

        ShortLink existingLink = new ShortLink();
        existingLink.setTenantId(tenantId);
        existingLink.setOriginalUrl(url);
        existingLink.setShortCode(existingCode);
        existingLink.setExpiresAt(Instant.now().plus(10, ChronoUnit.DAYS).toEpochMilli());

        when(shortLinkRepository.findByOriginalUrlAndTenantId(url, tenantId))
                .thenReturn(Optional.of(existingLink));

        String firstCode = service.createOrGetShortCode(url, null, tenantId, domain);
        String secondCode = service.createOrGetShortCode(url, null, tenantId, domain);

        assertEquals(existingCode, firstCode);
        assertEquals(existingCode, secondCode);
        verify(shortLinkRepository, never()).save(any());
    }

    @Test
    void testNewShortLinkCreatedWhenNotFound() {
        String tenantId = "tenant1";
        String url = "http://newsite.com";
        String domain = "http://short.ly";

        when(shortLinkRepository.findByOriginalUrlAndTenantId(url, tenantId))
                .thenReturn(Optional.empty());

        String code = service.createOrGetShortCode(url, null, tenantId, domain);

        // Verify save was called for new entry
        verify(shortLinkRepository, times(1)).save(any(ShortLink.class));
    }

    @Test
    void testExpiredLinkThrowsException() {
        String tenantId = "tenant1";
        String shortCode = "expired123";

        ShortLink expired = new ShortLink();
        expired.setTenantId(tenantId);
        expired.setShortCode(shortCode);
        expired.setOriginalUrl("http://oldsite.com");
        expired.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());

        when(shortLinkRepository.findByShortCodeAndTenantId(shortCode, tenantId))
                .thenReturn(Optional.of(expired));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getByShortCode(shortCode, tenantId));

        assertEquals("Short link expired", ex.getMessage());
    }

    @Test
    void testNotFoundLinkThrowsException() {
        String tenantId = "tenant1";
        String shortCode = "missing123";

        when(shortLinkRepository.findByShortCodeAndTenantId(shortCode, tenantId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getByShortCode(shortCode, tenantId));

        assertEquals("Short link not found", ex.getMessage());
    }

}
