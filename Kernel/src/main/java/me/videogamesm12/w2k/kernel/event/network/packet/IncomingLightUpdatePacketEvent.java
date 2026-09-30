package me.videogamesm12.w2k.kernel.event.network.packet;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>IncomingLightUpdatePacketEvent</h1>
 * <p>Event that is updated and called when the client receives a "light update" packet from a server.</p>
 * @implNote    This only gets called on versions 1.16 and newer.
 */
@Getter
public class IncomingLightUpdatePacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private int chunkX;
    private int chunkZ;

    public IncomingLightUpdatePacketEvent update(final int chunkX,
                                                 final int chunkZ)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            return this;
        }
    }
}
