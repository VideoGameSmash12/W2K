package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class EntityRenderEvent extends CustomEvent
{
    private final Object sync = new Object();

    private EntityInterface entity;

    public EntityRenderEvent update(final EntityInterface entity)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.entity = entity;
            return this;
        }
    }
}
