package me.videogamesm12.w2k.kernel.event.network.packet;

import com.google.gson.JsonElement;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class IncomingOpenScreenPacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private String type;
    private JsonElement name;
    private int syncId;

    public IncomingOpenScreenPacketEvent update(final String type,
                                                final JsonElement name,
                                                final int syncId)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.type = type;
            this.name = name;
            this.syncId = syncId;
            return this;
        }
    }
}
