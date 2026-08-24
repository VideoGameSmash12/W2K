package me.videogamesm12.w2k.drivers.v1_20_1.mixin.wrapper;

import me.videogamesm12.w2k.kernel.data.IBlockEntityEntry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(BlockEntity.class)
public abstract class BlockEntityWrapper implements IBlockEntityEntry
{
    @Shadow
    public abstract BlockEntityType<?> getType();

    @Shadow
    public abstract BlockPos getPos();

    @Shadow
    @Nullable
    protected World world;

    @Shadow
    public abstract NbtCompound toInitialChunkDataNbt();

    @Override
    public String w2k$type()
    {
        return Objects.requireNonNull(Registries.BLOCK_ENTITY_TYPE.getKey(getType()))
                .map(key -> key.getValue().toString())
                .orElse("minecraft:unknown");
    }

    @Override
    public int w2k$x()
    {
        return getPos().getX();
    }

    @Override
    public int w2k$y()
    {
        return getPos().getY();
    }

    @Override
    public int w2k$z()
    {
        return getPos().getZ();
    }

    @Override
    public String w2k$data()
    {
        return toInitialChunkDataNbt().toString();
    }
}
