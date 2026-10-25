package com.jayantirai.urlshortener.analytics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LinkAnalyticsResponse(
        String code,
        long clickCount,
        List<LocalDateTime> recentClicks
) {}