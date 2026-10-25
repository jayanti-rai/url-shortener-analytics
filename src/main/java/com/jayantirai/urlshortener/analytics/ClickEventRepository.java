package com.jayantirai.urlshortener.analytics;

import com.jayantirai.urlshortener.link.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByLink(ShortLink link);
    List<ClickEvent> findTop20ByLinkOrderByClickedAtDesc(ShortLink link);
}