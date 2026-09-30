package me.videogamesm12.w2k.kernel.abstraction.graphics;

import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

/**
 * <h1>CompiledDrawableObject</h1>
 * <p>An interface representing a compiled form of {@link DrawableObject} which can be added to in-game screens or
 *  rendered as overlays in the HUD.</p>
 * @param <T>   An implementation of {@link DrawableObject}
 */
public interface CompiledDrawableObject<T extends DrawableObject<T>>
{
    /**
     * Gets the source object which this compiled form comes from.
     * @return  The implementation of {@link DrawableObject}.
     */
    T source();

    /**
     * Gets whether to update both this object and the source object.
     * @return  {@code boolean}
     */
    boolean shouldUpdate();

    /**
     * Updates the relevant portions of this object and the source object.
     */
    void update();

    /**
     * Gets whether this object should be discarded and replaced with a completely new compiled object.
     * @return  {@code boolean}
     */
    boolean shouldRegenerate();
}
