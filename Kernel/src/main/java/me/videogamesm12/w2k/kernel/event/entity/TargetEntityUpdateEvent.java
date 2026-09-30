package me.videogamesm12.w2k.kernel.event.entity;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>TargetEntityUpdateEvent</h1>
 * <p>Event that is called when the client changes the targeted entity.</p>
 */
public class TargetEntityUpdateEvent extends CustomEvent
{
    private final Object sync = new Object();
    @Getter
    private EntityInterface target;

    public TargetEntityUpdateEvent update(final EntityInterface target)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.target = target;
            return this;
        }
    }
}
