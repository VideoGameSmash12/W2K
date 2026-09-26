package me.videogamesm12.w2k.kernel.graphics;

import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;

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
