package com.jayantirai.urlshortener.link;

import com.jayantirai.urlshortener.analytics.dto.LinkAnalyticsResponse;
import com.jayantirai.urlshortener.link.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/links")
    public LinkResponse create(@Valid @RequestBody CreateLinkRequest request) {
        return linkService.create(request);
    }

    @GetMapping("/links/{code}/analytics")
    public LinkAnalyticsResponse analytics(@PathVariable String code) {
        return linkService.analytics(code);
    }

    @GetMapping("/analytics/top")
    public List<LinkResponse> top(@RequestParam(defaultValue = "10") int limit) {
        return linkService.top(limit);
    }

    @GetMapping("/r/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest request) {
        String url = linkService.resolveAndTrack(code, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }
}