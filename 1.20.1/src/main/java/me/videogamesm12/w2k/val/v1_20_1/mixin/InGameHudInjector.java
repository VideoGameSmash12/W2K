package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.graphics.AbstractGraphicsHandler;
import me.videogamesm12.w2k.kernel.event.render.OverlayRequestEvent;
import me.videogamesm12.w2k.kernel.abstraction.graphics.CompiledDrawableObject;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudInjector
{
    @Unique
    private final OverlayRequestEvent overlayRequestEvent = new OverlayRequestEvent();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void hookRender(DrawContext context, float tickDelta, CallbackInfo ci)
    {
        final AbstractGraphicsHandler handler = W2K.getInstance().getVersionAbstractionLayer().graphicsHandler();

        W2K.getEventBus().post(overlayRequestEvent.update());
        overlayRequestEvent.getSubmitted().stream()
                .filter(handler::hasCompiler)
                .forEach(overlay ->
                {
                    final CompiledDrawableObject<?> compiled = (CompiledDrawableObject<?>) handler.getCompiler(overlay.getClass()).apply(overlay);
                    if (compiled instanceof Drawable drawable)
                    {
                        if (overlay.compiled().shouldUpdate())
                        {
                            overlay.compiled().update();
                        }

                        drawable.render(context, -1, -1, tickDelta);
                    }
                });
    }
}
