package me.videogamesm12.w2k.val.v1_21_11.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrashReport.class)
public class CrashReportInjector
{
    @Inject(method = "asString(Lnet/minecraft/util/crash/ReportType;Ljava/util/List;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/crash/CrashReport;addDetails(Ljava/lang/StringBuilder;)V", shift = At.Shift.AFTER))
    public void injectOurCrashReportData(CallbackInfoReturnable<String> cir, @Local StringBuilder stringBuilder)
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
