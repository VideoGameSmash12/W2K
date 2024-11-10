package me.videogamesm12.w2k.drivers.v1_21_2.mixin.accessor;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientWorld.class)
public interface ClientWorldAccessor
{
    @Accessor
    public Map<MapIdComponent, MapState> getMapStates();
}
