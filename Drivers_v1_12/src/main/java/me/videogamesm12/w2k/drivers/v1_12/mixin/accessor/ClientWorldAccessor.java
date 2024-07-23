package me.videogamesm12.w2k.drivers.v1_12.mixin.accessor;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor
{
    @Accessor("world")
    public Set<Entity> getEntities();
}
