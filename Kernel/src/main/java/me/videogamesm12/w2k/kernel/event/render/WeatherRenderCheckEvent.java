package me.videogamesm12.w2k.kernel.event.render;

import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>WeatherRenderCheckEvent</h1>
 * <p>Event that is updated and called when the client is about to render the weather effects in-game.</p>
 */
public class WeatherRenderCheckEvent extends CustomEvent
{
    public final Object sync = new Object();

    public WeatherRenderCheckEvent update()
    {
        synchronized (sync)
        {
            setCancelled(false);
            return this;
        }
    }
}
