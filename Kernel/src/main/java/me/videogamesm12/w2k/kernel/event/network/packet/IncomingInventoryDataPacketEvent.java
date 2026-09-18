package me.videogamesm12.w2k.kernel.event.network.packet;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.util.List;

@Getter
public class IncomingInventoryDataPacketEvent extends CustomEvent
{
    private final Object sync = new Object();

    private int syncId;
    private int revision;
    private List<ItemStackInterface> contents;

    public IncomingInventoryDataPacketEvent update(int syncId, int revision, List<ItemStackInterface> contents)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.syncId = syncId;
            this.revision = revision;
            this.contents = contents;
            return this;
        }
    }
}
