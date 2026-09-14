package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;

import java.util.Optional;

public interface LivingEntityInterface extends EntityInterface
{
    Optional<ItemStackInterface> w2k$getStackInMainHand();

    Optional<ItemStackInterface> w2k$getStackInOffHand();
}
