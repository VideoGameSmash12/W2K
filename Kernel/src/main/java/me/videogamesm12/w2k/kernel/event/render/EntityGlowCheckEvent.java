package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
public class EntityGlowCheckEvent extends CustomEvent
{
    private final Object sync = new Object();

    @Setter
    private Boolean outcome;
    private EntityInterface entity;

    public EntityGlowCheckEvent update(final EntityInterface entity)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.outcome = null;
            this.entity = entity;
            return this;
        }
    }
}
