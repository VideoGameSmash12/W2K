package me.videogamesm12.w2k.drivers.v1_20_1.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrashReport.class)
public class CrashReportMixin
{
    @Inject(method = "asString", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReport;addStackTrace(Ljava/lang/StringBuilder;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectOurCrashReportData(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder)
    {
        if (ExperimentManager.isExperimentEnabled(Experiment.KERNEL_APPEND_DETAILS_TO_CRASH_REPORTS))
        {
            final PopulateCrashReportEvent event = new PopulateCrashReportEvent();
            W2K.getEventBus().post(event);
            if (!event.getDetails().isEmpty())
            {
                stringBuilder.append("\n\n--- W2K ---\n");
                stringBuilder.append(event.getDetails());
            }
        }
    }
}
