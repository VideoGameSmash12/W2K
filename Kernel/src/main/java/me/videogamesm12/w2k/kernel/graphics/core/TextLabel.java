package me.videogamesm12.w2k.kernel.graphics.core;

import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.graphics.Alignable;
import me.videogamesm12.w2k.kernel.graphics.Alignment;
import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import me.videogamesm12.w2k.kernel.graphics.DrawableObject;
import net.kyori.adventure.text.Component;

public class TextLabel implements DrawableObject<TextLabel>, Alignable
{
    @Getter
    @Setter
    private Component source;
    private CompiledDrawableObject<TextLabel> compiled;

    @Setter
    private int x;
    @Setter
    private int y;
    @Setter
    private int width;
    @Setter
    private int height;

    private Alignment horizontal;
    private Alignment vertical;

    public TextLabel(final Component source,
                     final int x,
                     final int y)
    {
        this.source = source;
        this.x = x;
        this.y = y;
        this.horizontal = Alignment.LEAST;
        this.vertical = Alignment.LEAST;
    }

    public TextLabel(final Component source,
                     final int x,
                     final int y,
                     final Alignment horizontal,
                     final Alignment vertical)
    {
        this.source = source;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

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
    public Alignment horizontalAlignment()
    {
        return horizontal;
    }

    @Override
    public void horizontalAlignment(final Alignment alignment)
    {
        this.horizontal = alignment;
    }

    @Override
    public Alignment verticalAlignment()
    {
        return vertical;
    }

    @Override
    public void verticalAlignment(final Alignment alignment)
    {
        this.vertical = alignment;
    }

    @Override
    public CompiledDrawableObject<TextLabel> compiled()
    {
        return compiled;
    }

    @Override
    public void compiled(CompiledDrawableObject<TextLabel> compiledObject)
    {
        this.compiled = compiledObject;
    }
}
