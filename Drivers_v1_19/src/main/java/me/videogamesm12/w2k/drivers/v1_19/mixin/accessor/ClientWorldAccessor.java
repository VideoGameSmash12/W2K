package me.videogamesm12.w2k.drivers.v1_19.mixin.accessor;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor
{
    @Accessor
    public Map<String, MapState> getMapStates();
}
