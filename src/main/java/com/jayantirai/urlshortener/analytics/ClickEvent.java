package com.jayantirai.urlshortener.analytics;

import com.jayantirai.urlshortener.link.ShortLink;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "click_event")
public class ClickEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "link_id")
    private ShortLink link;

    @Column(nullable = false)
    private LocalDateTime clickedAt;

    private String ipAddress;
    private String userAgent;

    public ClickEvent() {
    }

    public ClickEvent(Long id, ShortLink link, LocalDateTime clickedAt, String ipAddress, String userAgent) {
        this.id = id;
        this.link = link;
        this.clickedAt = clickedAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public static ClickEventBuilder builder() {
        return new ClickEventBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public ShortLink getLink() {
        return this.link;
    }

    public LocalDateTime getClickedAt() {
        return this.clickedAt;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLink(ShortLink link) {
        this.link = link;
    }

    public void setClickedAt(LocalDateTime clickedAt) {
        this.clickedAt = clickedAt;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @PrePersist
    void prePersist() {
        if (clickedAt == null) clickedAt = LocalDateTime.now();
    }

    public static class ClickEventBuilder {
        private Long id;
        private ShortLink link;
        private LocalDateTime clickedAt;
        private String ipAddress;
        private String userAgent;

        ClickEventBuilder() {
        }

        public ClickEventBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ClickEventBuilder link(ShortLink link) {
            this.link = link;
            return this;
        }

        public ClickEventBuilder clickedAt(LocalDateTime clickedAt) {
            this.clickedAt = clickedAt;
            return this;
        }

        public ClickEventBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public ClickEventBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public ClickEvent build() {
            return new ClickEvent(this.id, this.link, this.clickedAt, this.ipAddress, this.userAgent);
        }
    }
}