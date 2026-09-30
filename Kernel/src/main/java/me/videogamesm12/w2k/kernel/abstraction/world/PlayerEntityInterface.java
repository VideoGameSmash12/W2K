package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;

/**
 * <h1>PlayerEntityInterface</h1>
 * <p>A wrapper interface for {@code PlayerEntity} or {@code Player} (depending on your mappings) instance implemented
 *  using Mixins.</p>
 * @implNote    This interface inherits all the methods from {@link LivingEntityInterface} and (by extension)
 *              {@link EntityInterface}. However, those methods don't need to be implemented in an implementation of
 *              this class since they would already be implemented by your implementations of such interfaces.
 */
public interface PlayerEntityInterface extends LivingEntityInterface
{
    /**
     * Returns whether this player is in Creative Mode.
     * @return  True if the player's gamemode is set to Creative.
     */
    boolean w2k$isCreative();

    /**
     * Returns the {@link GameProfileInterface} associated with this entity.
     * @return  {@link GameProfileInterface}
     */
    GameProfileInterface w2k$getGameProfile();
}
