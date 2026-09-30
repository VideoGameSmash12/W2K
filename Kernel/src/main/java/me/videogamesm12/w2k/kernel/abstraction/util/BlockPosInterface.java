package me.videogamesm12.w2k.kernel.abstraction.util;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

/**
 * <h1>BlockPosInterface</h1>
 * <p>A wrapper interface for {@code BlockPos} instances implemented using Mixins.
 */
public interface BlockPosInterface extends ObjectInterface
{
    /**
     * Gets the X coordinate.
     * @return  {@code int}
     */
    int w2k$x();

    /**
     * Gets the Y coordinate.
     * @return  {@code int}
     */
    int w2k$y();

    /**
     * Gets the Z coordinate.
     * @return  {@code int}
     */
    int w2k$z();

    /**
     * Formats this BlockPos' coordinates into comma-separated values using {@link String#format(String, Object...)}.
     * @return  {@link String}
     */
    default String w2k$toString()
    {
        return String.format("%d, %d, %d", w2k$x(), w2k$y(), w2k$z());
    }
}
