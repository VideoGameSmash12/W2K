package me.videogamesm12.w2k.val.v1_21_11.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.world.PlayerEntityInterface;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityWrapper implements PlayerEntityInterface
{
    @Shadow
    public abstract boolean isCreative();

    @Override
    public boolean w2k$isCreative()
    {
        return isCreative();
    }
}
