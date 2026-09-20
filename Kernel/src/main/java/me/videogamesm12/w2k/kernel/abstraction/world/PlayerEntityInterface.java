package me.videogamesm12.w2k.kernel.abstraction.world;

import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;

public interface PlayerEntityInterface extends LivingEntityInterface
{
    boolean w2k$isCreative();

    GameProfileInterface w2k$getGameProfile();
}
