package me.videogamesm12.w2k.val.v26_2.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.graphics.AbstractGraphicsHandler;
import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import me.videogamesm12.w2k.kernel.event.render.OverlayRequestEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.components.Renderable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudInjector
{
    @Unique
    private final OverlayRequestEvent overlayRequestEvent = new OverlayRequestEvent();

    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractCrosshair(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER))
    public void hookRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        final AbstractGraphicsHandler handler = W2K.getInstance().getVersionAbstractionLayer().graphicsHandler();

        W2K.getEventBus().post(overlayRequestEvent.update());
        overlayRequestEvent.getSubmitted().stream()
                .filter(handler::hasCompiler)
                .forEach(overlay ->
                {
                    final CompiledDrawableObject<?> compiled = (CompiledDrawableObject<?>) handler.getCompiler(overlay.getClass()).apply(overlay);
                    if (compiled instanceof Renderable renderable)
                    {
                        if (overlay.compiled().shouldUpdate())
                        {
                            overlay.compiled().update();
                        }

                        renderable.extractRenderState(graphics, -1, -1, deltaTracker.getGameTimeDeltaTicks());
                    }
                });
    }
}
