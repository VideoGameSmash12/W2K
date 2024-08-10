package me.videogamesm12.w2k.integrator.mixins.bleachhack;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModCategoryMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.partitions.bleachhack.BleachHackModuleMenu;
import me.videogamesm12.w2k.integrator.partitions.bleachhack.BleachHackSettingsDialog;
import me.videogamesm12.w2k.kernel.W2K;
import org.apache.commons.lang3.StringUtils;
import org.bleachhack.BleachHack;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.module.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.util.Arrays;

@Mixin(BleachHack.class)
public class BleachHackMixin
{
    @Inject(method = "postInit", at = @At("TAIL"), remap = false)
    public void afterInit(CallbackInfo ci)
    {
        // First things first, we add a warning to the logs to notify the user in the logs about the issue
        W2K.getLogger().warn("Greetings from W2K's developer - please do not report bugs/crashes caused by BleachHack  "
                + "in this instance unless you can verify that the issue happens even when W2K is not installed.");
        W2K.getLogger().warn("W2K has a module called 'Integrator' which injects code into BleachHack to integrate it "
                + "into the Blackbox more cleanly, which changes its behavior and screws with the stacktraces. If you cannot "
                + "recreate a crash in an environment without W2K, you should instead report the issue on W2K's GitHub "
                + "at https://github.com/VideoGameSmash12/W2K/issues.");

        // Creates the menu
        final PModMenu<BleachHack> menu = new PModMenu<>("BleachHack", BleachHack.getInstance());

        // Adds the mod icon
        menu.addModIconIfPresent("bleachhack");

        // Adds all of the BleachHack modules to the menu
        Arrays.stream(ModuleCategory.values()).forEach(category ->
        {
            PModCategoryMenu cMenu = new PModCategoryMenu(StringUtils.capitalize(category.name().toLowerCase()));
            ModuleManager.getModulesInCat(category).forEach(module -> cMenu.addModule(new BleachHackModuleMenu(module)));
            menu.addSubMenu(cMenu);
        });

        // Adds separator for non-module shenanigans
        menu.addSeparator();

        // Adds menu option for Settings
        final JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener((e) -> new BleachHackSettingsDialog().setVisible(true));
        menu.add(settingsMenuItem);

        // Queues the menu for the Blackbox
        W2KMenu.queueModMenu(menu);
    }
}
