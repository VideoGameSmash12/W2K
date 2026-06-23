package me.videogamesm12.w2k.drivers.v26_1.mixin.accessor;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface IGHAccessor
{
    @Accessor
    DebugScreenOverlay getDebugOverlay();
}
