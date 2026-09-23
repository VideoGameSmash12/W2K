package me.videogamesm12.w2k.val.v26_1_x.wrappers.util;

import me.videogamesm12.w2k.kernel.abstraction.util.SessionInterface;
import net.minecraft.client.User;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(User.class)
public abstract class SessionWrapper implements SessionInterface
{
    @Shadow
    @Final
    private String name;

    @Shadow
    @Final
    private UUID uuid;

    @Override
    public String w2k$getUsername()
    {
        return name;
    }

    @Override
    public String w2k$getUuidString()
    {
        return uuid.toString();
    }

    @Override
    public @Nullable UUID w2k$getUuid()
    {
        return uuid;
    }
}
