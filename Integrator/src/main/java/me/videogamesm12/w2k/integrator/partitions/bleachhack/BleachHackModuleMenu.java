package me.videogamesm12.w2k.integrator.partitions.bleachhack;

import me.videogamesm12.w2k.integrator.core.gui.PModModuleMenu;
import org.bleachhack.module.Module;

import javax.swing.*;

public class BleachHackModuleMenu extends PModModuleMenu<Module>
{
    public BleachHackModuleMenu(Module module)
    {
        super("integrator:bleachhack", module);

        // If we have any settings, let's go ham.
        if (!module.getSettings().isEmpty())
        {
            addSeparator();
            final JMenuItem moduleSettings = new JMenuItem("Settings");
            moduleSettings.addActionListener((e) -> new BleachHackModuleSettingsDialog(module).setVisible(true));
            add(moduleSettings);
        }
    }

    @Override
    public String getName()
    {
        return getModule().getName();
    }

    @Override
    public String getDescription()
    {
        return getModule().getDesc();
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
