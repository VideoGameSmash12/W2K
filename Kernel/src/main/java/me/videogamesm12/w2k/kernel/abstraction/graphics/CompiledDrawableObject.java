package me.videogamesm12.w2k.kernel.abstraction.graphics;

import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

public interface CompiledDrawableObject<T extends DrawableObject<T>>
{
    T source();

    boolean shouldUpdate();

    void update();

    boolean shouldRegenerate();
}
