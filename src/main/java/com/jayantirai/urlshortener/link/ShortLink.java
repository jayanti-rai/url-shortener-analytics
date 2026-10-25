package com.jayantirai.urlshortener.link;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_link")
public class ShortLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private long clickCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ShortLink() {
    }

    public ShortLink(Long id, String originalUrl, String code, LocalDateTime expiresAt, long clickCount, LocalDateTime createdAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.code = code;
        this.expiresAt = expiresAt;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
    }

    public static ShortLinkBuilder builder() {
        return new ShortLinkBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getOriginalUrl() {
        return this.originalUrl;
    }

    public String getCode() {
        return this.code;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public long getClickCount() {
        return this.clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public static class ShortLinkBuilder {
        private Long id;
        private String originalUrl;
        private String code;
        private LocalDateTime expiresAt;
        private long clickCount;
        private LocalDateTime createdAt;

        ShortLinkBuilder() {
        }

        public ShortLinkBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ShortLinkBuilder originalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
            return this;
        }

        public ShortLinkBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ShortLinkBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public ShortLinkBuilder clickCount(long clickCount) {
            this.clickCount = clickCount;
            return this;
        }

        public ShortLinkBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ShortLink build() {
            return new ShortLink(this.id, this.originalUrl, this.code, this.expiresAt, this.clickCount, this.createdAt);
        }
    }
}