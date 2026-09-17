package me.videogamesm12.w2k.val.v1_20_1.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientWorldInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.MapStateInterface;
import me.videogamesm12.w2k.val.v1_20_1.mixin.WorldAccessor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.map.MapState;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Mixin(ClientWorld.class)
public abstract class ClientWorldWrapper implements ClientWorldInterface
{
    @Shadow
    public abstract Iterable<Entity> getEntities();

    @Shadow
    @Final
    private Map<String, MapState> mapStates;

    @Override
    public List<EntityInterface> w2k$getEntities()
    {
        return StreamSupport.stream(getEntities().spliterator(), false).map(EntityInterface.class::cast)
                .toList();
    }

    @Override
    public List<BlockEntityInterface> w2k$getBlockEntities()
    {
        return ((WorldAccessor) this).getBlockEntityTickers().stream()
                .filter(ticker -> !ticker.isRemoved())
                .filter(ticker -> ticker.getPos() != null)
                .map(ticker -> World.class.cast(this).getBlockEntity(ticker.getPos()))
                .filter(Objects::nonNull)
                .map(BlockEntityInterface.class::cast)
                .toList();
    }

    @Override
    public Map<String, MapStateInterface> w2k$getMapStates()
    {
        return (Map) mapStates;
    }
}
