package me.videogamesm12.w2k.integrator.mixins.meteor_client;

import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.renderer.text.FontFamily;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(FontFamily.class)
public interface FontFamilyAccessor
{
	@Accessor
	List<FontFace> getFonts();

}
