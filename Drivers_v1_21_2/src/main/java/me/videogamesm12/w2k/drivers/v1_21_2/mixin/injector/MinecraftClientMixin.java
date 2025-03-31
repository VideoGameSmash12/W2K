package me.videogamesm12.w2k.drivers.v1_21_2.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "saveCrashReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/Bootstrap;println(Ljava/lang/String;)V", shift = At.Shift.BEFORE, ordinal = -1))
    private static void catchCrashReport(File runDir, CrashReport crashReport, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 1) Path crashReportFile)
    {
        final ClientCrashedEvent event = new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile.toFile());
        Supervisor.getEventBus().post(event);
    }
}
