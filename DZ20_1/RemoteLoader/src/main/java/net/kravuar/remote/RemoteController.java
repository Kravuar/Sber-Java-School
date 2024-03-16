package net.kravuar.remote;

import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/remote")
@RequiredArgsConstructor
class RemoteController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final RemoteProps remoteProps;
    private final ConcurrentMap<String, CacheEntry> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .<String, CacheEntry>build()
            .asMap();

    @GetMapping("get/{url}")
    ResponseEntity<?> getRemote(@PathVariable("url") String url) {
        CacheEntry cacheEntry = cache.computeIfAbsent(
                url,
                key -> {
                    URI uri = URI.create(URLDecoder.decode(url, StandardCharsets.UTF_8));
                    return new CacheEntry(LocalDateTime.now(), restTemplate.getForEntity(uri, String.class).getBody());
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add(remoteProps.cacheHeaderName(), cacheEntry.cachedAt().toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(cacheEntry.object());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> malformedHandler(IllegalArgumentException exception) {
        return ResponseEntity
                .badRequest()
                .body(exception.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    ResponseEntity<?> ioHandler(RestClientException exception) {
        return ResponseEntity
                .internalServerError()
                .body(exception.getMessage());
    }
}
