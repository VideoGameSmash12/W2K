package me.videogamesm12.w2k.kernel.graphics;

import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;

/**
 * <h1>DrawableObject</h1>
 * <p>An interface for 2D objects that can be drawn on the screen.</p>
 * @param <T>   An implementation of DrawableObject
 */
public interface DrawableObject<T extends DrawableObject<T>>
{
    int x();

    int y();

    int width();

    int height();

    default int weight()
    {
        return 0;
    }

    CompiledDrawableObject<T> compiled();

    void compiled(CompiledDrawableObject<T> compiledObject);
}
