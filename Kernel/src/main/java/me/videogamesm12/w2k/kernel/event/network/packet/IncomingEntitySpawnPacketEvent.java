package me.videogamesm12.w2k.kernel.event.network.packet;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.util.UUID;

@Getter
public class IncomingEntitySpawnPacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private int entityId;
    private UUID entityUuid;
    private String entityType;
    private double x;
    private double y;
    private double z;

    public IncomingEntitySpawnPacketEvent update(final int entityId,
                                                 final UUID entityUuid,
                                                 final String entityType,
                                                 final double x,
                                                 final double y,
                                                 final double z)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.entityId = entityId;
            this.entityUuid = entityUuid;
            this.entityType = entityType;
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
    }
}
