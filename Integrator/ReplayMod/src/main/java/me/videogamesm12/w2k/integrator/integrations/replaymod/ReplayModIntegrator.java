package me.videogamesm12.w2k.integrator.integrations.replaymod;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.IModIntegrator;
import me.videogamesm12.w2k.integrator.core.IntegratorMetadata;
import me.videogamesm12.w2k.integrator.integrations.replaymod.menu.ReplayModMenu;
import me.videogamesm12.w2k.kernel.W2K;

@IntegratorMetadata(required = "replaymod")
public class ReplayModIntegrator extends IModIntegrator
{
    @Override
    public void onStart()
    {
        // First things first, we add a warning to the logs to notify the user in the logs about the issue
        W2K.getLogger().warn("Greetings from W2K's developer - please do not report bugs/crashes caused by the Replay Mod "
                + "in this instance unless you can verify that the issue happens even when W2K is not installed.");
        W2K.getLogger().warn("W2K has a module called 'Integrator' which injects code into the Replay Mod to integrate it "
                + "into the Blackbox more cleanly, which changes its behavior and screws with the stacktraces. If you cannot "
                + "recreate a crash in an environment without W2K, you should instead report the issue on W2K's GitHub "
                + "at https://github.com/VideoGameSmash12/W2K/issues.");

        // Queue up our menu
        W2KMenu.queueModMenu(new ReplayModMenu());
    }
}
