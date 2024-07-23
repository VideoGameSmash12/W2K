package me.videogamesm12.w2k.drivers.v1_8.mixin.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor
{
    @Invoker("getSavedEntityId")
    String getSavedEntityId();
}
