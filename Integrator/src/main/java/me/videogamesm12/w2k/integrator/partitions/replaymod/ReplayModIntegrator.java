package me.videogamesm12.w2k.integrator.partitions.replaymod;

import com.replaymod.core.Module;
import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;

public class ReplayModIntegrator implements Module
{
    @Override
    public void initClient()
    {
        W2KMenu.queueModMenu(new ReplayModMenu());
    }
}
