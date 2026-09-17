package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCleanedUpAfterCrashEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@Mixin(MinecraftClient.class)
public class MinecraftClientInjector
{
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
}
