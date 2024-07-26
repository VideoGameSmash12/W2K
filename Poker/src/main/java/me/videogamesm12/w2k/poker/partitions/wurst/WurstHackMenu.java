package me.videogamesm12.w2k.poker.partitions.wurst;

import me.videogamesm12.w2k.poker.Poker;
import me.videogamesm12.w2k.poker.core.gui.PModModuleMenu;
import net.wurstclient.hack.Hack;

import javax.swing.*;

public class WurstHackMenu extends PModModuleMenu<Hack>
{
    public WurstHackMenu(Hack module)
    {
        super("poker:wurst", module);
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
            Poker.getLogger().warn("WTF HOW IS IT NULL?");
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
