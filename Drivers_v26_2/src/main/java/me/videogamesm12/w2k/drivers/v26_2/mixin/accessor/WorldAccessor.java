package me.videogamesm12.w2k.drivers.v26_2.mixin.accessor;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Level.class)
public interface WorldAccessor
{
    @Accessor
    public List<TickingBlockEntity> getBlockEntityTickers();
}
