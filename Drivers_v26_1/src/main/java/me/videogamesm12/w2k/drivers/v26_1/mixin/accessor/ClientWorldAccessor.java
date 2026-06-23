package me.videogamesm12.w2k.drivers.v26_1.mixin.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientLevel.class)
public interface ClientWorldAccessor
{
    @Accessor
    public Map<MapId, MapItemSavedData> getMapData();
}
