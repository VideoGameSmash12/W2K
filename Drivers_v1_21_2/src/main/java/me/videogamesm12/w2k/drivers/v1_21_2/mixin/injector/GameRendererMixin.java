package me.videogamesm12.w2k.drivers.v1_21_2.mixin.injector;

import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci)
    {
        // Refuses to render anything period.
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci)
    {
        if (Supervisor.getConfig().getRenderingSettings().isGameRenderingDisabled()
                || Supervisor.getConfig().getRenderingSettings().isWorldRenderingDisabled())
        {
            ci.cancel();
        }
    }
}
