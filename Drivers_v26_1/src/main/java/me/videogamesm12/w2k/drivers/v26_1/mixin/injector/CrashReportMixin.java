package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import net.minecraft.CrashReport;
import net.minecraft.ReportType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CrashReport.class)
public class CrashReportMixin
{
    @Inject(method = "getFriendlyReport(Lnet/minecraft/ReportType;Ljava/util/List;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/CrashReport;getDetails(Ljava/lang/StringBuilder;)V", shift = At.Shift.AFTER))
    public void injectOurCrashReportData(ReportType reportType, List<String> extraComments, CallbackInfoReturnable<String> cir, @Local StringBuilder builder)
    {
        final PopulateCrashReportEvent event = new PopulateCrashReportEvent();
        W2K.getEventBus().post(event);
        if (!event.getDetails().isEmpty())
        {
            builder.append("\n\n--- W2K ---\n");
            builder.append(event.getDetails());
        }
    }
}
