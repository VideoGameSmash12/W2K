package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>EntityRenderCheckEvent</h1>
 * <p>Event that is updated and called when the client is about to render an entity.</p>
 * <p>To prevent the game from rendering the entity, cancel this event.</p>
 */
@Getter
public class EntityRenderCheckEvent extends CustomEvent
{
    private final Object sync = new Object();

    private EntityInterface entity;

    public EntityRenderCheckEvent update(final EntityInterface entity)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.entity = entity;
            return this;
        }
    }
}
