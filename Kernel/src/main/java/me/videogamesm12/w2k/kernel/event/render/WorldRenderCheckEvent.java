package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>WorldRenderCheckEvent</h1>
 * <p>Event that is updated and called when the client is about to render the in-game world.</p>
 * <p>To prevent the game from rendering the in-game world, cancel this event.</p>
 */
@Getter
public class WorldRenderCheckEvent extends CustomEvent
{
    private final Object sync = new Object();
    private float tickDelta;

    public WorldRenderCheckEvent update(final float tickDelta)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.tickDelta = tickDelta;
            return this;
        }
    }
}
