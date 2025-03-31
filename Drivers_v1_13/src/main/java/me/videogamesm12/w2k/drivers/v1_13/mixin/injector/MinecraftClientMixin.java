package me.videogamesm12.w2k.drivers.v1_13.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.flags.Flags;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.fabricmc.loader.api.FabricLoader;
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
    @Inject(method = "initializeGame", at = @At(value = "RETURN"))
    public void onStart(CallbackInfo ci)
    {
        W2K.getEventBus().post(new ClientStartedEvent(this));
    }

    @Inject(method = "stop", at = @At(value = "INVOKE", target =
            "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V", shift = At.Shift.AFTER, remap = false))
    public void onStop(CallbackInfo ci)
    {
        W2K.getEventBus().post(new ClientStoppedEvent(this));
    }

    @Inject(method = "printCrashReport", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE, ordinal = -1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void catchCrashReport(CrashReport crashReport, CallbackInfo ci, File crashReportFolder, File crashReportFile)
    {
        final ClientCrashedEvent event = new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile);
        Supervisor.getEventBus().post(event);
    }
}
