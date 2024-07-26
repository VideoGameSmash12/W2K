package me.videogamesm12.w2k.drivers.v1_20_6.mixin.accessor;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface IGHAccessor
{
    @Accessor
    DebugHud getDebugHud();
}
