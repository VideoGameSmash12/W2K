package me.videogamesm12.w2k.kernel.abstraction.util;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * <h1>SessionInterface</h1>
 * <p>A wrapper interface for {@code Session} or {@code User} (depending on your mappings) instances implemented using
 *  Mixins.
 */
public interface SessionInterface extends ObjectInterface
{
    /**
     * Gets the username for this session.
     * @return      {@link String}
     */
    String w2k$getUsername();

    /**
     * Gets the UUID for this session as a String.
     * @implNote    This is guaranteed to return a non-null value, but whether the value will always be a valid UUID
     *              depends on the version of the game. Older versions (e.g. 1.20.1) can return invalid UUIDs, whereas
     *              newer versions (e.g. 1.21.11+) will never return an invalid UUID.
     * @return      {@link String}
     */
    String w2k$getUuidString();

    /**
     * <p>Gets the UUID for this session.</p>
     * @return      {@link UUID}
     */
    @Nullable
    UUID w2k$getUuid();
}
