package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapping.gui;

import me.videogamesm12.w2k.kernel.wrapper.gui.WrappedScreen;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public class ScreenWrappingMixin implements WrappedScreen
{
}
