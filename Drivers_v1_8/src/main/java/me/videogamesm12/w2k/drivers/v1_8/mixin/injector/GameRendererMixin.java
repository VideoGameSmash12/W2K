package me.videogamesm12.w2k.drivers.v1_8.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(float tickDelta, long nanoTime, CallbackInfo ci)
    {
        // Refuses to render anything period.
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void postRender(float tickDelta, long nanoTime, CallbackInfo ci)
    {
        Watchdog.LAST_RENDERED_TIME = System.currentTimeMillis();
    }

    @Inject(method = "renderWorld(FJ)V", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(float tickDelta, long limitTime, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isWorldRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
