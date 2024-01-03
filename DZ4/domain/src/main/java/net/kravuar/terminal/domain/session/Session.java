package net.kravuar.terminal.domain.session;

import net.kravuar.terminal.domain.card.CardDetails;

import java.time.LocalDateTime;

public record Session(
        CardDetails cardDetails,
        String accessToken,
        LocalDateTime expiresAt
) {
    public boolean isActive() {
        return expiresAt.isAfter(LocalDateTime.now());
    }
}
