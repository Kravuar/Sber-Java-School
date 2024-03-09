package net.kravuar.cache.key;

import lombok.NonNull;

@FunctionalInterface
public interface KeyGeneratorResolver {
    /**
     * Resolves a {@link KeyGenerator} by associated name.
     *
     * @param name key generator identifier.
     * @return associated key generator, or {@code null} if it wasn't found.
     */
    KeyGenerator resolve(@NonNull String name);
}
