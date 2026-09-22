package me.videogamesm12.w2k.kernel.graphics;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
public class Box implements DrawableObject
{
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
}
