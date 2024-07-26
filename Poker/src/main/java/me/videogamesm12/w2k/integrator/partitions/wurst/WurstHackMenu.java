package me.videogamesm12.w2k.integrator.partitions.wurst;

import me.videogamesm12.w2k.integrator.core.gui.PModModuleMenu;
import me.videogamesm12.w2k.kernel.W2K;
import net.wurstclient.hack.Hack;

import javax.swing.*;

public class WurstHackMenu extends PModModuleMenu<Hack>
{
    public WurstHackMenu(Hack module)
    {
        super("integrator:wurst", module);
        if (!module.getSettings().isEmpty())
        {
            addSeparator();
            final JMenuItem settingsMenuItem = new JMenuItem("Settings");
            settingsMenuItem.addActionListener((e) ->
            {
                new WurstHackSettingsDialog(module).setVisible(true);
            });
            add(settingsMenuItem);
        }
    }

    @Override
    public String getName()
    {
        if (getModule() != null)
        {
            return getModule().getName();
        }
        else
        {
            W2K.getLogger().warn("WTF HOW IS IT NULL?");
            return "VIDEO FIX ME NOW NOW NOW NOW";
        }
    }

    @Override
    public String getDescription()
    {
        // An issue is present where some descriptions don't work correctly.
        return "";
    }

    @Override
    public void setModuleEnabled(boolean value)
    {
        getModule().setEnabled(value);
    }

    @Override
    public boolean isModuleEnabled()
    {
        return getModule().isEnabled();
    }
}
