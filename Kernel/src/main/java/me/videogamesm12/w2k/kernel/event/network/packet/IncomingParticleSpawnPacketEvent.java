package me.videogamesm12.w2k.kernel.event.network.packet;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class IncomingParticleSpawnPacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private int count;
    private double x;
    private double y;
    private double z;
    private float speed;

    public IncomingParticleSpawnPacketEvent update(final int count,
                                                   final double x,
                                                   final double y,
                                                   final double z,
                                                   final float speed)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.count = count;
            this.x = x;
            this.y = y;
            this.z = z;
            this.speed = speed;
            return this;
        }
    }
}
