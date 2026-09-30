package me.videogamesm12.w2k.val.v26_2.wrappers.world;

import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.PlayerEntityInterface;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerWrapper implements PlayerEntityInterface
{
    @Shadow
    @Final
    private GameProfile gameProfile;

    @Shadow
    public abstract boolean isCreative();

    @Override
    public boolean w2k$isCreative()
    {
        return isCreative();
    }

    @Override
    public GameProfileInterface w2k$getGameProfile()
    {
        return GameProfileInterface.class.cast(gameProfile);
    }
}
