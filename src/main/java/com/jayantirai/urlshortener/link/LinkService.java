package com.jayantirai.urlshortener.link;

import com.jayantirai.urlshortener.analytics.ClickEvent;
import com.jayantirai.urlshortener.analytics.ClickEventRepository;
import com.jayantirai.urlshortener.analytics.dto.LinkAnalyticsResponse;
import com.jayantirai.urlshortener.link.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.springframework.http.HttpStatus.*;

@Service
public class LinkService {
    private static final Logger log = LoggerFactory.getLogger(LinkService.class);

    private final ShortLinkRepository shortLinkRepository;
    private final ClickEventRepository clickEventRepository;
    private final StringRedisTemplate redis;

    public LinkService(ShortLinkRepository shortLinkRepository, ClickEventRepository clickEventRepository, StringRedisTemplate redis) {
        this.shortLinkRepository = shortLinkRepository;
        this.clickEventRepository = clickEventRepository;
        this.redis = redis;
    }

    @Transactional
    public LinkResponse create(CreateLinkRequest request) {
        String code = generateCode();
        ShortLink saved = shortLinkRepository.save(ShortLink.builder()
                .code(code)
                .originalUrl(request.originalUrl())
                .expiresAt(request.expiresAt())
                .clickCount(0)
                .build());

        cachePut(code, saved.getOriginalUrl());
        return toResponse(saved);
    }

    @Transactional
    public String resolveAndTrack(String code, String ip, String ua) {
        String cached = cacheGet(code);
        ShortLink link = shortLinkRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Short link not found"));

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(GONE, "Short link expired");
        }

        link.setClickCount(link.getClickCount() + 1);
        clickEventRepository.save(ClickEvent.builder()
                .link(link)
                .ipAddress(ip)
                .userAgent(ua)
                .build());

        if (cached == null) {
            cachePut(code, link.getOriginalUrl());
        }
        return link.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public LinkAnalyticsResponse analytics(String code) {
        ShortLink link = shortLinkRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Short link not found"));

        var recent = clickEventRepository.findTop20ByLinkOrderByClickedAtDesc(link)
                .stream().map(ClickEvent::getClickedAt).toList();

        return new LinkAnalyticsResponse(code, link.getClickCount(), recent);
    }

    @Transactional(readOnly = true)
    public List<LinkResponse> top(int limit) {
        return shortLinkRepository.findAll(PageRequest.of(0, Math.min(limit, 50), Sort.by(Sort.Direction.DESC, "clickCount")))
                .map(this::toResponse)
                .toList();
    }

    private LinkResponse toResponse(ShortLink l) {
        return new LinkResponse(l.getCode(), l.getOriginalUrl(), l.getExpiresAt(), l.getClickCount(), l.getCreatedAt(), "http://localhost:8081/r/" + l.getCode());
    }

    private String generateCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 7; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
            code = sb.toString();
        } while (shortLinkRepository.existsByCode(code));
        return code;
    }

    private String cacheKey(String code) {
        return "short:" + code;
    }

    // Redis is a best-effort cache; the database remains the source of truth.
    private String cacheGet(String code) {
        try {
            return redis.opsForValue().get(cacheKey(code));
        } catch (DataAccessException ex) {
            log.warn("Redis unavailable, skipping cache read for {}: {}", code, ex.getMessage());
            return null;
        }
    }

    private void cachePut(String code, String originalUrl) {
        try {
            redis.opsForValue().set(cacheKey(code), originalUrl, Duration.ofHours(24));
        } catch (DataAccessException ex) {
            log.warn("Redis unavailable, skipping cache write for {}: {}", code, ex.getMessage());
        }
    }
}