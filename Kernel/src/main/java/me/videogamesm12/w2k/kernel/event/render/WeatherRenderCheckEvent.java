package me.videogamesm12.w2k.kernel.event.render;

import me.videogamesm12.w2k.kernel.event.CustomEvent;

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
