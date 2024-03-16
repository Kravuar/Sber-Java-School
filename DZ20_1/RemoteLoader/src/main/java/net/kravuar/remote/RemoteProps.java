package net.kravuar.remote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("remote")
@Validated
record RemoteProps(
        @NotNull
        @NotBlank
        String cacheHeaderName
) {
}
