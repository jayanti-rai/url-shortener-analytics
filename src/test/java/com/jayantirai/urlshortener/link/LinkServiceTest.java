package com.jayantirai.urlshortener.link;

import com.jayantirai.urlshortener.analytics.ClickEventRepository;
import com.jayantirai.urlshortener.link.dto.CreateLinkRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock ShortLinkRepository shortLinkRepository;
    @Mock ClickEventRepository clickEventRepository;
    @Mock StringRedisTemplate redis;
    @Mock ValueOperations<String, String> ops;

    @InjectMocks LinkService linkService;

    @Test
    void create_success() {
        when(redis.opsForValue()).thenReturn(ops);
        when(shortLinkRepository.existsByCode(anyString())).thenReturn(false);
        when(shortLinkRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = linkService.create(new CreateLinkRequest("https://example.com", null));

        assertNotNull(res.code());
        assertTrue(res.originalUrl().contains("example.com"));
        verify(shortLinkRepository).save(any());
    }

    @Test
    void resolve_notFound() {
        when(redis.opsForValue()).thenReturn(ops);
        when(shortLinkRepository.findByCode("abc")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> linkService.resolveAndTrack("abc", "1.1.1.1", "ua"));
    }
}
