package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class BlockEntityRenderEvent extends CustomEvent
{
    private final Object sync = new Object();

    private Object blockEntity;

    public BlockEntityRenderEvent update(final Object blockEntity)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.blockEntity = blockEntity;
            return this;
        }
    }
}
