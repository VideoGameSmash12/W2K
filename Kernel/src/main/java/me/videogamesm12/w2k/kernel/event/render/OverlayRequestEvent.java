package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.w2k.kernel.graphics.DrawableObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <h1>OverlayRequestEvent</h1>
 * <p>Event that is updated and called while the client is rendering the in-game HUD for the purpose of figuring out
 *  what {@link DrawableObject}s need to be rendered as overlays.</p>
 */
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
