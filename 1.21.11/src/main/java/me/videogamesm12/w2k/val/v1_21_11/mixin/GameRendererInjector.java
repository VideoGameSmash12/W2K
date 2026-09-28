package me.videogamesm12.w2k.val.v1_21_11.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.entity.TargetEntityUpdateEvent;
import me.videogamesm12.w2k.kernel.event.render.GameRenderEvent;
import me.videogamesm12.w2k.kernel.event.render.WorldRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererInjector
{
    @Shadow
    @Final
    private MinecraftClient client;
    @Unique
    private final WorldRenderEvent worldRenderEvent = new WorldRenderEvent();
    @Unique
    private final TargetEntityUpdateEvent targetEntityUpdateEvent = new TargetEntityUpdateEvent();
    @Unique
    private final GameRenderEvent gameRenderEvent = new GameRenderEvent();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(RenderTickCounter renderTickCounter, boolean bl, CallbackInfo ci)
    {
        W2K.getEventBus().post(gameRenderEvent.update(renderTickCounter.getDynamicDeltaTicks()));

        if (gameRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE))
    public void callTargetEntityUpdateEvent(float tickDelta, CallbackInfo ci)
    {
        W2K.getEventBus().post(targetEntityUpdateEvent.update((EntityInterface) client.targetedEntity));
    }

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(RenderTickCounter renderTickCounter, CallbackInfo ci)
    {
        W2K.getEventBus().post(worldRenderEvent.update(renderTickCounter.getDynamicDeltaTicks()));

        if (worldRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
