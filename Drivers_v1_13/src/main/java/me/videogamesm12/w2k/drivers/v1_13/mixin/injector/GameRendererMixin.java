package me.videogamesm12.w2k.drivers.v1_13.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.minecraft.class_4218;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_4218.class)
public class GameRendererMixin
{
    @Inject(method = "method_19061", at = @At("HEAD"), cancellable = true)
    public void startRender(float tickDelta, long nanoTime, boolean bl, CallbackInfo ci)
    {
        // Refuses to render anything period.
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "method_19074", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(float tickDelta, long limitTime, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isWorldRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
