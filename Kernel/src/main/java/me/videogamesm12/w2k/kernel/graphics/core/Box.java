package me.videogamesm12.w2k.kernel.graphics.core;

import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

import java.awt.*;

@Setter
public class Box implements DrawableObject<Box>
{
    private CompiledDrawableObject<Box> compiled;

    private int x;
    private int y;
    private int width;
    private int height;
    @Getter
    private Color color;

    @Override
    public int x()
    {
        return x;
    }

    @Override
    public int y()
    {
        return y;
    }

    @Override
    public int width()
    {
        return width;
    }

    @Override
    public int height()
    {
        return height;
    }

    @Override
    public CompiledDrawableObject<Box> compiled()
    {
        return compiled;
    }

    @Override
    public void compiled(final CompiledDrawableObject<Box> compiledObject)
    {
        this.compiled = compiledObject;
    }
}
