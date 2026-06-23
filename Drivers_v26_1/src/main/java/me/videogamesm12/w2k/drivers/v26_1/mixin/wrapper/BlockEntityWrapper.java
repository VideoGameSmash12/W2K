package me.videogamesm12.w2k.drivers.v26_1.mixin.wrapper;

import me.videogamesm12.w2k.kernel.data.IBlockEntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(BlockEntity.class)
public abstract class BlockEntityWrapper implements IBlockEntityEntry
{
    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public abstract CompoundTag getUpdateTag(HolderLookup.Provider registries);

    @Shadow
    @Nullable
    public abstract Level getLevel();

    @Shadow
    public abstract BlockEntityType<?> getType();

    @Override
    public String w2k$type()
    {
        return Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(getType())).toString();
    }

    @Override
    public int w2k$x()
    {
        return getBlockPos().getX();
    }

    @Override
    public int w2k$y()
    {
        return getBlockPos().getY();
    }

    @Override
    public int w2k$z()
    {
        return getBlockPos().getZ();
    }

    @Override
    public String w2k$data()
    {
        return getUpdateTag(Objects.requireNonNull(getLevel()).registryAccess()).toString();
    }
}
