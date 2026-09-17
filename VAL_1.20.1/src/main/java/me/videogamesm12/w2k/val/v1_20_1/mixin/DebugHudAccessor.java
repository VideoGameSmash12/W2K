package me.videogamesm12.w2k.val.v1_20_1.mixin;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(DebugHud.class)
public interface DebugHudAccessor
{
    @Invoker("getLeftText")
    List<String> getLeftText();

    @Invoker("getRightText")
    List<String> getRightText();
}
