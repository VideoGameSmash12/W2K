package me.videogamesm12.w2k.kernel.event.network.packet;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class IncomingExplosionPacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private float radius;
    private double x;
    private double y;
    private double z;

    public IncomingExplosionPacketEvent update(final float radius,
                                               final double x,
                                               final double y,
                                               final double z)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.radius = radius;
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
    }
}
