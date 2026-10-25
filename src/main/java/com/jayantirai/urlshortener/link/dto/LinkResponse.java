package com.jayantirai.urlshortener.link.dto;

import java.time.LocalDateTime;

public record LinkResponse(
        String code,
        String originalUrl,
        LocalDateTime expiresAt,
        long clickCount,
        LocalDateTime createdAt,
        String shortUrl
) {}