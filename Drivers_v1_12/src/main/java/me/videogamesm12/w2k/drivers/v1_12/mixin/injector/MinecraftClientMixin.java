package me.videogamesm12.w2k.drivers.v1_12.mixin.injector;

import me.videogamesm12.w2k.kernel.Experiments;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.flags.Flags;
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
    /**
     * <p>This forces the Supervisor to properly shut down after the client has crashed if a mod like Not Enough Crashes is not present.</p>
     * <p>If the crash was intentionally caused by the Supervisor, this reverts also the flag if Not Enough Crashes was detected to avoid a potential softlock.</p>
     * @param ci    CallbackInfo
     */
    @Inject(method = "cleanUpAfterCrash", at = @At("RETURN"))
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
    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;tick()V", shift = At.Shift.BEFORE))
    public void intentionallyCrash(CallbackInfo ci)
    {
        if (Supervisor.getInstance().getFlags().isSupposedToCrash())
        {
            W2K.getLogger().info("Hey, want to see a magic trick?");
            int lol = 0 / 0;
        }
    }

    @Inject(method = "printCrashReport", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", shift = At.Shift.BEFORE, ordinal = -1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void catchCrashReport(CrashReport crashReport, CallbackInfo ci, File crashReportFolder, File crashReportFile)
    {
        if (Experiments.experimentEnabled(Experiments.SUPERVISOR_CATCHES_CRASHES))
        {
            final ClientCrashedEvent event = new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile);

            Supervisor.getEventBus().post(event);
        }
    }
}
