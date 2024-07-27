package me.videogamesm12.w2k.integrator.partitions.meteor;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModCategoryMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class MeteorEntrypoint extends MeteorAddon
{
    @Override
    public void onInitialize()
    {
        // Create our menu
        final PModMenu<MeteorClient> menu = new PModMenu<>("Meteor Client", MeteorClient.INSTANCE);

        // Sets the mod icon
        menu.addModIconIfPresent("meteor-client");

        // Loops through the categories in memory
        Modules.loopCategories().forEach(category ->
        {
            final PModCategoryMenu categoryMenu = new PModCategoryMenu(category.name);
            Modules.get().getGroup(category).forEach(module -> categoryMenu.addModule(new MeteorModuleMenu(module)));
            menu.addSubMenu(categoryMenu);
        });

        // Adds the menu
        W2KMenu.queueModMenu(menu);
    }

    public String getPackage()
    {
        return null;
    }
}
