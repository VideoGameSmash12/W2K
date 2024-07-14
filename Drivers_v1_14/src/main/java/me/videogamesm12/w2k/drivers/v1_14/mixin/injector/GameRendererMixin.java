package me.videogamesm12.w2k.drivers.v1_14.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci)
    {
        // Refuses to render anything period.
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(float tickDelta, long endTime, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isWorldRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
