package me.videogamesm12.w2k.val.v26_1_x.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.render.GameRenderEvent;
import me.videogamesm12.w2k.kernel.event.render.WorldRenderEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererInjector
{
    @Unique
    private final WorldRenderEvent worldRenderEvent = new WorldRenderEvent();
    @Unique
    private final GameRenderEvent gameRenderEvent = new GameRenderEvent();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci)
    {
        W2K.getEventBus().post(gameRenderEvent.update(deltaTracker.getGameTimeDeltaTicks()));

        if (gameRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(DeltaTracker deltaTracker, CallbackInfo ci)
    {
        W2K.getEventBus().post(worldRenderEvent.update(deltaTracker.getGameTimeDeltaTicks()));

        if (worldRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
