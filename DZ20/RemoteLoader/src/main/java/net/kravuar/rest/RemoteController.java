package net.kravuar.rest;

import com.google.common.cache.CacheBuilder;
import net.kravuar.domain.RemoteResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/remote")
class RemoteController {
    public static final String CACHED_AT_HEADER = "CACHED_AT";

    private final ConcurrentMap<URI, RemoteResult> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(4)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .<URI, RemoteResult>build()
            .asMap();

    @GetMapping("get/{url}")
    ResponseEntity<?> getRemote(@PathVariable("url") String url) {
        RemoteResult remoteResult = getContent(URLDecoder.decode(url, StandardCharsets.UTF_8));
        return new ResponseEntity<>(
                remoteResult.getBody(),
                remoteResult.getHeaders(),
                remoteResult.getStatus()
        );
    }

    private RemoteResult getContent(String source) {
        try {
            URI uri = URI.create(source);
            URL url = uri.toURL();

            // Get existing entry, or create a new one
            return cache.computeIfAbsent(uri, key -> {
                RemoteResult.RemoteResultBuilder builder = RemoteResult.builder();

                try {
                    Object content = url.getContent();
                    builder.body(content);
                    builder.status(HttpStatus.OK);
                } catch (IOException e) {
                    builder.status(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(CACHED_AT_HEADER, LocalDateTime.now().toString());

                builder.headers(httpHeaders);
                return builder.build();
            });
        } catch (IllegalArgumentException | MalformedURLException e) {
            // No need to cache this
            return RemoteResult.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}
