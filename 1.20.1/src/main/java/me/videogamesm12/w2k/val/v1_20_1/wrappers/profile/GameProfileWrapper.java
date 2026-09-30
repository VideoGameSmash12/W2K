package me.videogamesm12.w2k.val.v1_20_1.wrappers.profile;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;
import me.videogamesm12.w2k.kernel.abstraction.profile.PropertyInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(GameProfile.class)
public abstract class GameProfileWrapper implements GameProfileInterface
{
    @Shadow
    public abstract String getName();

    @Shadow
    @Final
    private PropertyMap properties;

    @Shadow
    public abstract UUID getId();

    @Override
    public String w2k$name()
    {
        return getName();
    }

    @Override
    public UUID w2k$uuid()
    {
        return getId();
    }

    @Override
    public Multimap<String, PropertyInterface> w2k$properties()
    {
        return Multimap.class.cast(properties);
    }
}
