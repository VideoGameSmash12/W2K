package me.videogamesm12.w2k.kernel.event.render;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>BlockEntityRenderCheckEvent</h1>
 * <p>Event that is updated and called when the client is about to render a block entity.</p>
 * <p>To prevent the game from rendering a block entity, cancel this event.</p>
 */
@Getter
public class BlockEntityRenderCheckEvent extends CustomEvent
{
    private final Object sync = new Object();

    private BlockEntityInterface blockEntity;
    private Object blockEntityState;

    public BlockEntityRenderCheckEvent update(final BlockEntityInterface blockEntity, final Object blockEntityState)
    {
        synchronized (sync)
        {
            setCancelled(false);
            this.blockEntity = blockEntity;
            this.blockEntityState = blockEntityState;
            return this;
        }
    }
}
