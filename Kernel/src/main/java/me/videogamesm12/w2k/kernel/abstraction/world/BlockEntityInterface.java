package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>BlockEntityInterface</h1>
 * <p>A wrapper interface for {@code BlockEntity} instances implemented using Mixins.
 */
public interface BlockEntityInterface extends ObjectInterface
{
    /**
     * <p>Gets the block entity's type in the form of a stringified identifier.</p>
     * @return  {@link String}
     */
    String w2k$type();

    /**
     * Gets the block entity's current X position.
     * @return  {@code int}
     */
    int w2k$x();

    /**
     * Gets the block entity's current Y position.
     * @return  {@code int}
     */
    int w2k$y();

    /**
     * Gets the block entity's current Z position.
     * @return  {@code int}
     */
    int w2k$z();

    /**
     * Gets the block entity's NBT data.
     * @return  {@link String}
     */
    String w2k$data();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(w2k$type(),
                String.format("%s, %s, %s", w2k$x(), w2k$y(), w2k$z()),
                w2k$data());
    }
}
