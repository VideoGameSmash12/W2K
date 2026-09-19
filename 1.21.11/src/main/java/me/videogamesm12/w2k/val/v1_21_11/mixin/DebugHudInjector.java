package me.videogamesm12.w2k.val.v1_21_11.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.val.v1_21_11.VersionAbstractionLayer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudInjector
{
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V", ordinal = 1))
    public void cacheResults(DrawContext drawContext, CallbackInfo ci, @Local(ordinal = 0) List<String> leftLines)
    {
        VersionAbstractionLayer.class.cast(W2K.getInstance().getVersionAbstractionLayer())
                .getDebugHudHandler()
                .cacheLeftLines(leftLines);
    }
}
