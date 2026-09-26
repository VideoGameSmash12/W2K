package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OverlayRequestEvent extends CustomEvent
{
    private final Object sync = new Object();
    @Getter
    private final List<DrawableObject<?>> submitted = new ArrayList<>();

    public OverlayRequestEvent update()
    {
        synchronized (sync)
        {
            setCancelled(false);
            submitted.clear();
            return this;
        }
    }

    public void submit(DrawableObject<?> drawable)
    {
        submitted.add(drawable);
    }

    public void submitAll(Collection<DrawableObject<?>> drawable)
    {
        submitted.addAll(drawable);
    }
}
