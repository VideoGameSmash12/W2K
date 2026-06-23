package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.flags.Flags;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;

@Mixin(Minecraft.class)
public class MinecraftClientMixin
{
    /**
     * <p>Supervisor's freeze detection works by injecting some code at the tail-end of the game's rendering method to
     *  store a timestamp for when the last time a frame successfully rendered occurs, then periodically checking
     *  through another thread if it exceeds 5 seconds.</p>
     * <p>This code is what stores the timestamps.</p>
     * @param ci    CallbackInfo
     */
    @Inject(method = "runTick", at = @At("RETURN"))
    public void onPostRender(CallbackInfo ci)
    {
        if (Supervisor.getConfig().getWatchdogSettings().isFreezeDetectionEnabled())
        {
            Watchdog.LAST_RENDERED_TIME = System.currentTimeMillis();
        }
    }

    /**
     * <p>This forces the Supervisor to properly shut down after the client has crashed if a mod like Not Enough Crashes is not present.</p>
     * <p>If the crash was intentionally caused by the Supervisor, this reverts also the flag if Not Enough Crashes was detected to avoid a potential softlock.</p>
     * @param ci    CallbackInfo
     */
    @Inject(method = "emergencySave", at = @At("RETURN"))
    public void onCleanUpAfterCrash(CallbackInfo ci)
    {
        if (!FabricLoader.getInstance().isModLoaded("notenoughcrashes"))
        {
            Supervisor.getInstance().shutdown();
        }
        else
        {
            Flags flags = Supervisor.getInstance().getFlags();

            if (flags.isSupposedToCrash())
            {
                flags.setSupposedToCrash(false);
            }
        }
    }

    /**
     * <p>This will intentionally crash the client if the relevant flags are set.</p>
     * @param ci    CallbackInfo
     */
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;finishProfilers(ZLnet/minecraft/util/profiling/SingleTickProfiler;)V", shift = At.Shift.AFTER))
    public void intentionallyCrash(CallbackInfo ci)
    {
        if (Supervisor.getInstance().getFlags().isSupposedToCrash())
        {
            W2K.getLogger().info("Hey, want to see a magic trick?");
            int lol = 0 / 0;
        }
    }

    @Inject(method = "saveReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Bootstrap;realStdoutPrintln(Ljava/lang/String;)V", shift = At.Shift.BEFORE, ordinal = -1))
    private static void catchCrashReport(File runDir, CrashReport crashReport, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 1) Path crashReportFile)
    {
        final ClientCrashedEvent event = new ClientCrashedEvent(Minecraft.getInstance(), crashReport.getException(), crashReportFile.toFile());
        Supervisor.getEventBus().post(event);
    }
}
