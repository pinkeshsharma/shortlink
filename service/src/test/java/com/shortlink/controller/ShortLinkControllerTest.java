package com.shortlink.controller;

import com.shortlink.db.entity.ShortLink;
import com.shortlink.service.ShortLinkService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShortLinkController.class)
class ShortLinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class TestConfig {
        @Bean
        ShortLinkService shortLinkService() {
            return mock(ShortLinkService.class);
        }
    }

    @Autowired
    private ShortLinkService shortLinkService;

    @Test
    void testCreateShortLink() throws Exception {
        Mockito.when(shortLinkService.createOrGetShortCode(
                        anyString(), nullable(String.class), anyString(), anyString(), anyLong()))
                .thenReturn("abc123");

        String requestJson = """
        {
          "originalUrl": "http://example.com",
          "customCode": null,
          "domain": null
        }
        """;

        mockMvc.perform(post("/shorten")
                        .header("X-Tenant-ID", "defaultTenant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://localhost/s/abc123"));
    }

    @Test
    void testRedirect() throws Exception {
        ShortLink link = new ShortLink();
        link.setOriginalUrl("http://example.com");

        Mockito.when(shortLinkService.getByShortCode("abc123", "defaultTenant"))
                .thenReturn(link);

        mockMvc.perform(get("/s/abc123")
                .header("X-Tenant-ID", "defaultTenant"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "http://example.com"));
    }
}
