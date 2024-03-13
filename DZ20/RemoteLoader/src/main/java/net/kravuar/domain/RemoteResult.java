package net.kravuar.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class RemoteResult {
    private final HttpStatus status;
    private final Object body;
    private final HttpHeaders headers;
}
