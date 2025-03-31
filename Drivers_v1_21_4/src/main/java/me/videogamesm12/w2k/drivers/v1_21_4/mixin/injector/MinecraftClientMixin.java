package me.videogamesm12.w2k.drivers.v1_21_4.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
    @Inject(method = "saveCrashReport", at = @At(value = "INVOKE", target = "Lnet/minecraft/Bootstrap;println(Ljava/lang/String;)V", shift = At.Shift.BEFORE, ordinal = -1))
    private static void catchCrashReport(File runDir, CrashReport crashReport, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 1) Path crashReportFile)
    {
        final ClientCrashedEvent event = new ClientCrashedEvent(MinecraftClient.getInstance(), crashReport.getCause(), crashReportFile.toFile());
        Supervisor.getEventBus().post(event);
    }
}
