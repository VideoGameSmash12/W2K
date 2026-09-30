package me.videogamesm12.w2k.kernel.event.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>EntityInteractionEvent</h1>
 * <p>Event that is called when the player directly interacts with an entity by clicking it.</p>
 */
@Getter
@RequiredArgsConstructor
public class EntityInteractionEvent extends CustomEvent
{
    private final ClientPlayerEntityInterface clientPlayerEntity;
    private final EntityInterface target;
    private final boolean leftClick;
}
