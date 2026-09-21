package me.videogamesm12.w2k.val.v26_2.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(BlockEntity.class)
public abstract class BlockEntityWrapper implements BlockEntityInterface
{
    @Shadow
    @Final
    private BlockEntityType<?> type;

    @Shadow
    @Final
    protected BlockPos worldPosition;

    @Shadow
    public abstract CompoundTag getUpdateTag(HolderLookup.Provider registries);

    @Shadow
    @Nullable
    public abstract Level getLevel();

    @Override
    public String w2k$type()
    {
        return Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(type)
                .map(key -> key.identifier().toString())
                .orElse("minecraft:unknown"));
    }

    @Override
    public int w2k$x()
    {
        return worldPosition.getX();
    }

    @Override
    public int w2k$y()
    {
        return worldPosition.getY();
    }

    @Override
    public int w2k$z()
    {
        return worldPosition.getZ();
    }

    @Override
    public String w2k$data()
    {
        return getUpdateTag(Objects.requireNonNull(getLevel()).registryAccess()).toString();
    }
}
