package me.videogamesm12.w2k.integrator.partitions.meteor;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModCategoryMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;

import javax.swing.*;

public class MeteorEntrypoint extends MeteorAddon
{
    @Override
    public void onInitialize()
    {
        // Create our menu
        final PModMenu<MeteorClient> menu = new PModMenu<>(MeteorClient.NAME, MeteorClient.INSTANCE);

        // Sets the mod icon
        menu.addModIconIfPresent(MeteorClient.MOD_META.getId());

        // Loops through the categories in memory
        Modules.loopCategories().forEach(category ->
        {
            final PModCategoryMenu categoryMenu = new PModCategoryMenu(category.name);
            Modules.get().getGroup(category).forEach(module -> categoryMenu.addModule(new MeteorModuleMenu(module)));
            menu.addSubMenu(categoryMenu);
        });

        // Add separator
        menu.addSeparator();

        // Add Settings menu option
        final JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener((e) -> new MeteorSettingsDialog().setVisible(true));
        menu.add(settings);

        // Adds the menu
        W2KMenu.queueModMenu(menu);
    }

    public String getPackage()
    {
        return null;
    }
}
