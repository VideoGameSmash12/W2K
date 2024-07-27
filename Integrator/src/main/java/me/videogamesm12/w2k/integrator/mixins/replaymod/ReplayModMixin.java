package me.videogamesm12.w2k.integrator.mixins.replaymod;

import com.replaymod.core.Module;
import com.replaymod.core.ReplayMod;
import com.replaymod.core.ReplayModBackend;
import me.videogamesm12.w2k.integrator.partitions.replaymod.ReplayModIntegrator;
import me.videogamesm12.w2k.kernel.W2K;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ReplayMod.class)
public class ReplayModMixin
{
    @Shadow @Final private List<Module> modules;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void godHaveMercyOnMySoul(ReplayModBackend backend, CallbackInfo ci)
    {
        // First things first, we add a warning to the logs to notify the user in the logs about the issue
        W2K.getLogger().warn("Greetings from W2K's developer - please do not report bugs/crashes caused by the Replay Mod "
                + "in this instance unless you can verify that the issue happens even when W2K is not installed.");
        W2K.getLogger().warn("W2K has a module called 'Integrator' which injects code into the Replay Mod to integrate it "
                + "into the Blackbox more cleanly, which changes its behavior and screws with the stacktraces. If you cannot "
                + "recreate a crash in an environment without W2K, you should instead report the issue on W2K's GitHub "
                + "at https://github.com/VideoGameSmash12/W2K/issues.");

        this.modules.add(new ReplayModIntegrator());
    }
}
