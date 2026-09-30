package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>GameRenderCheckEvent</h1>
 * <p>Event that is updated and called when the client is about to render a frame.</p>
 * <p>To prevent the game from rendering at all, cancel this event.</p>
 */
@Getter
public class GameRenderCheckEvent extends CustomEvent
{
    private final Object sync = new Object();
    private float tickDelta;

    public GameRenderCheckEvent update(final float tickDelta)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.tickDelta = tickDelta;
            return this;
        }
    }
}
