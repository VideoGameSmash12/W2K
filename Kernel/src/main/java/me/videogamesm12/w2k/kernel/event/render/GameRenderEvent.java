package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class GameRenderEvent extends CustomEvent
{
    private final Object sync = new Object();
    private float tickDelta;

    public GameRenderEvent update(final float tickDelta)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.tickDelta = tickDelta;
            return this;
        }
    }
}
