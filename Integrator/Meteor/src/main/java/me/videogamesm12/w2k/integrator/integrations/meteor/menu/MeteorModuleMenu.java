package me.videogamesm12.w2k.integrator.integrations.meteor.menu;

import me.videogamesm12.w2k.integrator.core.gui.PModModuleMenu;
import meteordevelopment.meteorclient.systems.modules.Module;

import javax.swing.*;

public class MeteorModuleMenu extends PModModuleMenu<Module>
{
    public MeteorModuleMenu(Module module)
    {
        super("integrator:meteor", module);

        if (!module.settings.groups.isEmpty())
        {
            addSeparator();
            final JMenuItem settingsMenuItem = new JMenuItem("Settings");
            settingsMenuItem.addActionListener((e) -> new MeteorModuleSettingsDialog(module).setVisible(true));
            add(settingsMenuItem);
        }
    }

    @Override
    public String getName()
    {
        return getModule().title;
    }

    @Override
    public String getDescription()
    {
        return getModule().description;
    }

    @Override
    public void setModuleEnabled(boolean value)
    {
        if (isModuleEnabled() != value)
        {
            getModule().toggle();
        }
    }

    @Override
    public boolean isModuleEnabled()
    {
        return getModule().isActive();
    }
}
