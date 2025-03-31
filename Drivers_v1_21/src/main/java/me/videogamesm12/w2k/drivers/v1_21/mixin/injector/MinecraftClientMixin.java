package me.videogamesm12.w2k.drivers.v1_21.mixin.injector;

import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.File;
import java.nio.file.Path;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    /**
     * <p>Supervisor's freeze detection works by injecting some code at the tail-end of the game's rendering method to
     *  store a timestamp for when the last time a frame successfully rendered occurs, then periodically checking
     *  through another thread if it exceeds 5 seconds.</p>
     * <p>This code is what stores the timestamps.</p>
     * @param ci    CallbackInfo
     */
    @Inject(method = "render", at = @At("RETURN"))
    public void onPostRender(CallbackInfo ci)
    {
        if (Supervisor.getConfig().getWatchdogSettings().isFreezeDetectionEnabled())
        {
            Watchdog.LAST_RENDERED_TIME = System.currentTimeMillis();
        }
    }

    @Inject(method = "printCrashReport(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;Lnet/minecraft/util/crash/CrashReport;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE, ordinal = -1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void catchCrashReport(MinecraftClient client, File runDirectory, CrashReport crashReport, CallbackInfo ci, Path crashReportFolder, Path crashReportFile)
    {
        final ClientCrashedEvent event = new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile.toFile());
        Supervisor.getEventBus().post(event);
    }
}
