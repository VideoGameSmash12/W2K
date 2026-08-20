package me.videogamesm12.w2k.drivers.v26_2.mixin.accessor;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public interface DHAccessor
{
    //@Invoker("getLeftText")
    //List<String> getLeftText();
}
