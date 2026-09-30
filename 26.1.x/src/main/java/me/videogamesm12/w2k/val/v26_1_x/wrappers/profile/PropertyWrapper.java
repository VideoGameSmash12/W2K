package me.videogamesm12.w2k.val.v26_1_x.wrappers.profile;

import com.mojang.authlib.properties.Property;
import me.videogamesm12.w2k.kernel.abstraction.profile.PropertyInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Property.class)
public class PropertyWrapper implements PropertyInterface
{
    @Shadow
    @Final
    private String name;

    @Shadow
    @Final
    private String value;

    @Shadow
    @Final
    private String signature;

    @Override
    public String w2k$name()
    {
        return name;
    }

    @Override
    public String w2k$value()
    {
        return value;
    }

    @Override
    public String w2k$signature()
    {
        return signature;
    }
}
