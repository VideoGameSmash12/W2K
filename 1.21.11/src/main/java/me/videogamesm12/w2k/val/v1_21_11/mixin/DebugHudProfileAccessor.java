package me.videogamesm12.w2k.val.v1_21_11.mixin;

import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DebugHudProfile.class)
public interface DebugHudProfileAccessor
{
    @Accessor
    Map<Identifier, DebugHudEntryVisibility> getVisibilityMap();
}
