package me.videogamesm12.w2k.integrator.mixins.litematica;

import fi.dy.masa.litematica.Litematica;
import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.partitions.litematica.LitematicaSettingsDialog;
import me.videogamesm12.w2k.kernel.W2K;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;

@Mixin(Litematica.class)
public class LitematicaMixin
{
    @Inject(method = "onInitialize", at = @At("TAIL"), remap = false)
    public void injectInitialize(CallbackInfo ci)
    {
        // First things first, we add a warning to the logs to notify the user in the logs about the issue
        W2K.getLogger().warn("Greetings from W2K's developer - please do not report bugs/crashes caused by Litematica "
                + "in this instance unless you can verify that the issue happens even when W2K is not installed.");
        W2K.getLogger().warn("W2K has a module called 'Integrator' which injects code into the mod to integrate it "
                + "into the Blackbox more cleanly, which changes its behavior and screws with the stacktraces. If you cannot "
                + "recreate a crash in an environment without W2K, you should instead report the issue on W2K's GitHub "
                + "at https://github.com/VideoGameSmash12/W2K/issues.");

        final PModMenu<Litematica> menu = new PModMenu<>("Litematica", Litematica.class);
        menu.addModIconIfPresent("litematica");

        final JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener((e) -> new LitematicaSettingsDialog().setVisible(true));
        menu.add(settingsMenuItem);

        W2KMenu.queueModMenu(menu);
    }
}
