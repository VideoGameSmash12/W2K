package me.videogamesm12.w2k.val.v26_2.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientWorldInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.MapStateInterface;
import me.videogamesm12.w2k.val.v26_2.mixin.LevelAccessor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Mixin(ClientLevel.class)
public abstract class ClientWorldWrapper implements ClientWorldInterface
{
    @Shadow
    public abstract Iterable<Entity> entitiesForRendering();

    @Shadow
    @Final
    private Map<MapId, MapItemSavedData> mapData;

    @Override
    public List<EntityInterface> w2k$getEntities()
    {
        return StreamSupport.stream(entitiesForRendering().spliterator(), false).map(EntityInterface.class::cast)
                .toList();
    }

    @Override
    public List<BlockEntityInterface> w2k$getBlockEntities()
    {
        return ((LevelAccessor) this).getBlockEntityTickers().stream()
                .filter(ticker -> !ticker.isRemoved())
                .filter(ticker -> ticker.getPos() != null)
                .map(ticker -> Level.class.cast(this).getBlockEntity(ticker.getPos()))
                .filter(Objects::nonNull)
                .map(BlockEntityInterface.class::cast)
                .toList();
    }

    @Override
    public Map<String, MapStateInterface> w2k$getMapStates()
    {
        final Map<String, MapStateInterface> idToMap = new HashMap<>();
        mapData.forEach((id, state) ->
                idToMap.put(id.key(), (MapStateInterface) state));
        return idToMap;
    }
}
