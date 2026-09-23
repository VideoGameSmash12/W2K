package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCleanedUpAfterCrashEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowCheckEvent;
import me.videogamesm12.w2k.kernel.event.render.RenderCompleteEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@Mixin(MinecraftClient.class)
public class MinecraftClientInjector
{
    @Unique
    private final RenderCompleteEvent renderCompleteEvent = new RenderCompleteEvent();
    @Unique
    private final EntityGlowCheckEvent entityGlowCheckEvent = new EntityGlowCheckEvent();

    @Inject(method = "cleanUpAfterCrash", at = @At("RETURN"))
    public void callClientCleanedUpAfterCrashEvent(CallbackInfo ci)
    {
        W2K.getEventBus().post(new ClientCleanedUpAfterCrashEvent());
    }

    @Inject(method = "printCrashReport", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE, ordinal = -1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void callClientCrashedEvent(CrashReport crashReport, CallbackInfo ci, File crashReportFolder, File crashReportFile)
    {
        W2K.getEventBus().post(new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile));
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void callRenderCompleteEvent(CallbackInfo ci)
    {
        W2K.getEventBus().post(renderCompleteEvent.update(System.currentTimeMillis()));
    }

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    public void callEntityGlowCheck(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        W2K.getEventBus().post(entityGlowCheckEvent.update((EntityInterface) entity));
        if (entityGlowCheckEvent.getOutcome() != null)
        {
            cir.setReturnValue(entityGlowCheckEvent.getOutcome());
        }
    }
}
