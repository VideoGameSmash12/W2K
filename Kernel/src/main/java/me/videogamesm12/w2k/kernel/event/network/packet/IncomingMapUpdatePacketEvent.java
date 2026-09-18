package me.videogamesm12.w2k.kernel.event.network.packet;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class IncomingMapUpdatePacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private int id;
    private byte scale;
    private boolean locked;

    public IncomingMapUpdatePacketEvent update(int id,
                                               byte scale,
                                               boolean locked)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.id = id;
            this.scale = scale;
            this.locked = locked;
            return this;
        }
    }
}
