package com.shortlink.controller;

import com.shortlink.dto.ShortLinkRequest;
import com.shortlink.dto.ShortLinkResponse;
import com.shortlink.db.entity.ShortLink;
import com.shortlink.service.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    @Autowired
    public ShortLinkController(ShortLinkService shortLinkService) {
        this.shortLinkService = shortLinkService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<ShortLinkResponse> createShortLink(@RequestBody ShortLinkRequest request, HttpServletRequest httpRequest) {
        String tenantId = "defaultTenant";

        // Build base domain from the incoming request
        String domain = ServletUriComponentsBuilder.fromRequestUri(httpRequest)
                .replacePath(null)
                .build()
                .toUriString();

        String code = shortLinkService.createOrGetShortCode(
                request.getOriginalUrl(),
                request.getCustomCode(),
                tenantId,
                domain
        );

        String shortUrl = domain + "/s/" + code;
        return ResponseEntity.ok(new ShortLinkResponse(shortUrl));
    }

    @GetMapping({"/{shortCode}", "/s/{shortCode}"})
    public ResponseEntity<Void> redirect(
            @PathVariable("shortCode") String shortCode) {

        ShortLink link = shortLinkService.getByShortCode(shortCode, "defaultTenant");

        return ResponseEntity.status(301)
                .location(URI.create(link.getOriginalUrl()))
                .build();
    }

    @GetMapping("/links")
    public ResponseEntity<Page<ShortLink>> listLinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ShortLink> result = shortLinkService.getAllLinks(pageable);

        return ResponseEntity.ok(result);
    }

}
