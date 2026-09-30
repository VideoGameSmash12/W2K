package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>RenderCompleteEvent</h1>
 * <p>Event that is updated and called when the client finishes rendering a frame.</p>
 */
@Getter
public class RenderCompleteEvent extends CustomEvent
{
    private final Object sync = new Object();

    private long timestamp;

    public RenderCompleteEvent update(long timestamp)
    {
        synchronized (sync)
        {
            this.timestamp = timestamp;
            return this;
        }
    }
}
