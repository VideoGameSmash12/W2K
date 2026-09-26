package me.videogamesm12.w2k.val.v1_21_11.graphics.core;

import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import me.videogamesm12.w2k.kernel.graphics.core.Box;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;

public class CompiledBox implements CompiledDrawableObject<Box>, Drawable
{
    private final Box box;

    public CompiledBox(final Box box)
    {
        this.box = box;
    }

    @Override
    public Box source()
    {
        return box;
    }

    @Override
    public boolean shouldUpdate()
    {
        return false;
    }

    @Override
    public void update()
    {
    }

    @Override
    public boolean shouldRegenerate()
    {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        context.fill(box.x(), box.y(), box.x() + box.width(), box.y() + box.height(), box.getColor().getRGB());
    }
}
