package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.val.v1_20_1.graphics.OverlayRenderDispatcherImpl;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(InGameHud.class)
public class InGameHudInjector
{
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void hookRender(DrawContext context, float tickDelta, CallbackInfo ci)
    {
        OverlayRenderDispatcherImpl impl = OverlayRenderDispatcherImpl.class.cast(W2K.getInstance().getVersionAbstractionLayer().renderDispatcher());

        W2K.getInstance().getModuleManager().getIdRegistry().values().stream()
                .filter(WModule::isEnabled)
                .map(WModule::getOverlays)
                .flatMap(Collection::stream)
                .filter(overlay -> overlay.getShouldDisplay().test(overlay))
                .filter(overlay -> impl.isRendererRegistered(overlay.getId()))
                .forEach(overlay -> impl.getRenderer(overlay.getId()).renderOverlay(overlay, context));
    }
}
