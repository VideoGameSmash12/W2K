package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci)
    {
        // Refuses to render anything period.
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(DeltaTracker deltaTracker, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isWorldRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
