package net.kravuar.remote;

import java.time.LocalDateTime;

record CacheEntry(
        LocalDateTime cachedAt,
        Object object
) {
}
