package me.videogamesm12.w2k.drivers.v1_20_1.mixin.accessor;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(DebugHud.class)
public interface DHAccessor
{
    @Invoker("getLeftText")
    List<String> getLeftText();
}
