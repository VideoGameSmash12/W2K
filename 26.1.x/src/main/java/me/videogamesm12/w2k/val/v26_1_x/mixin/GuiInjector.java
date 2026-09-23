package me.videogamesm12.w2k.val.v26_1_x.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.val.v26_1_x.graphics.OverlayRenderDispatcherImpl;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(Gui.class)
public class GuiInjector
{
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    public void hookRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        OverlayRenderDispatcherImpl impl = OverlayRenderDispatcherImpl.class.cast(W2K.getInstance().getVersionAbstractionLayer().renderDispatcher());

        W2K.getInstance().getModuleManager().getIdRegistry().values().stream()
                .filter(WModule::isEnabled)
                .map(WModule::getOverlays)
                .flatMap(Collection::stream)
                .filter(overlay -> overlay.getShouldDisplay().test(overlay))
                .filter(overlay -> impl.isRendererRegistered(overlay.getId()))
                .forEach(overlay -> impl.getRenderer(overlay.getId()).renderOverlay(overlay, graphics));
    }
}
