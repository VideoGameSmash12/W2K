package me.videogamesm12.w2k.drivers.v1_21.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.ReportType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(CrashReport.class)
public class CrashReportMixin
{
    @Inject(method = "asString(Lnet/minecraft/util/crash/ReportType;Ljava/util/List;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReport;addDetails(Ljava/lang/StringBuilder;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void injectOurCrashReportData(ReportType type, List<String> extraInfo, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder)
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
