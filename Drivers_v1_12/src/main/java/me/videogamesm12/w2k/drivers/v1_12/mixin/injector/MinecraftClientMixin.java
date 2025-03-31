package me.videogamesm12.w2k.drivers.v1_12.mixin.injector;

import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Inject(method = "printCrashReport", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE, ordinal = -1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void catchCrashReport(CrashReport crashReport, CallbackInfo ci, File crashReportFolder, File crashReportFile)
    {
        final ClientCrashedEvent event = new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile);
        Supervisor.getEventBus().post(event);
    }
}
