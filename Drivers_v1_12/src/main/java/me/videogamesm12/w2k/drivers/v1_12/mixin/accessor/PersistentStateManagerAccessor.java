package me.videogamesm12.w2k.drivers.v1_12.mixin.accessor;

import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(PersistentStateManager.class)
public interface PersistentStateManagerAccessor
{
    @Accessor
    public Map<String, PersistentState> getStateMap();
}
