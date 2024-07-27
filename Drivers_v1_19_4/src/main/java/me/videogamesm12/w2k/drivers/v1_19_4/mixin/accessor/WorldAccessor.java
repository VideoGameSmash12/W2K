package me.videogamesm12.w2k.drivers.v1_19_4.mixin.accessor;

import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(World.class)
public interface WorldAccessor
{
    @Accessor
    public List<BlockEntityTickInvoker> getBlockEntityTickers();
}
