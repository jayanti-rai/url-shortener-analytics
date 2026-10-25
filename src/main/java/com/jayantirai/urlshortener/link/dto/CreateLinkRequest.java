package com.jayantirai.urlshortener.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateLinkRequest(
        @NotBlank
        @Pattern(regexp = "^(https?://).+", message = "URL must start with http:// or https://")
        String originalUrl,
        LocalDateTime expiresAt
) {}