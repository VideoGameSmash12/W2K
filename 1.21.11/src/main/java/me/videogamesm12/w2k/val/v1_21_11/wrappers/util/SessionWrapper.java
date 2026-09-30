package me.videogamesm12.w2k.val.v1_21_11.wrappers.util;

import me.videogamesm12.w2k.kernel.abstraction.util.SessionInterface;
import net.minecraft.client.session.Session;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Session.class)
public abstract class SessionWrapper implements SessionInterface
{
    @Shadow
    @Final
    private String username;

    @Shadow
    @Final
    private UUID uuid;

    @Override
    public String w2k$getUsername()
    {
        return username;
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
