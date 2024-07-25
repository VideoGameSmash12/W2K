package me.videogamesm12.w2k.drivers.v1_13.mixin.accessor;

import net.minecraft.class_4068;
import net.minecraft.class_4070;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(class_4070.class)
public interface EnigmaClass4070Accessor
{
    @Accessor("field_19776")
    Map<DimensionType, class_4068> getPersistentStateManagers();
}
