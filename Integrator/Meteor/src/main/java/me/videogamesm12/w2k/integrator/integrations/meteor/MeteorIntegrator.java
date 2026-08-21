package me.videogamesm12.w2k.integrator.integrations.meteor;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.IModIntegrator;
import me.videogamesm12.w2k.integrator.core.IntegratorMetadata;
import me.videogamesm12.w2k.integrator.core.gui.PModCategoryMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.integrations.meteor.menu.MeteorModuleMenu;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Modules;

@IntegratorMetadata(required = "meteor-client")
public class MeteorIntegrator extends IModIntegrator
{
    @Override
    public void onStart()
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

        // Adds the menu
        W2KMenu.queueModMenu(menu);
    }
}
