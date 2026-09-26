package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.entity.TargetEntityUpdateEvent;
import me.videogamesm12.w2k.kernel.event.render.GameRenderEvent;
import me.videogamesm12.w2k.kernel.event.render.WorldRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
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
    private final GameRenderEvent gameRenderEvent = new GameRenderEvent();
    @Unique
    private final TargetEntityUpdateEvent targetEntityUpdateEvent = new TargetEntityUpdateEvent();
    @Unique
    private final WorldRenderEvent worldRenderEvent = new WorldRenderEvent();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void startRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci)
    {
        W2K.getEventBus().post(gameRenderEvent.update(tickDelta));

        if (gameRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }

    @Inject(method = "updateTargetedEntity", at = @At("RETURN"))
    public void callTargetEntityUpdateEvent(float tickDelta, CallbackInfo ci)
    {
        W2K.getEventBus().post(targetEntityUpdateEvent.update((EntityInterface) client.targetedEntity));
    }

    @Inject(method = "renderWorld", at = @At("HEAD"), cancellable = true)
    public void injectRenderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci)
    {
        W2K.getEventBus().post(worldRenderEvent.update(tickDelta));

        if (worldRenderEvent.isCancelled())
        {
            ci.cancel();
        }
    }
}
