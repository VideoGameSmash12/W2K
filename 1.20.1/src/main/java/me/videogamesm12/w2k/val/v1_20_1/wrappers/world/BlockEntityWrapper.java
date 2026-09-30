package me.videogamesm12.w2k.val.v1_20_1.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.world.BlockEntityInterface;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
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
    protected BlockPos pos;

    @Shadow
    public abstract NbtCompound toInitialChunkDataNbt();

    @Override
    public String w2k$type()
    {
        return Objects.requireNonNull(Registries.BLOCK_ENTITY_TYPE.getKey(type))
                .map(key -> key.getValue().toString())
                .orElse("minecraft:unknown");
    }

    @Override
    public int w2k$x()
    {
        return pos.getX();
    }

    @Override
    public int w2k$y()
    {
        return pos.getY();
    }

    @Override
    public int w2k$z()
    {
        return pos.getZ();
    }

    @Override
    public String w2k$data()
    {
        return toInitialChunkDataNbt().toString();
    }
}
