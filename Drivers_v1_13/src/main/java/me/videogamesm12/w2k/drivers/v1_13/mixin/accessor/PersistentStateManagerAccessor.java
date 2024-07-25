package me.videogamesm12.w2k.drivers.v1_13.mixin.accessor;

import net.minecraft.class_4068;
import net.minecraft.world.PersistentState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(class_4068.class)
public interface PersistentStateManagerAccessor
{
    @Accessor("field_19752")
    public Map<String, PersistentState> getStateMap();
}
