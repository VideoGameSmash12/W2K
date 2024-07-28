package me.videogamesm12.w2k.integrator.mixins.litematica;

import fi.dy.masa.litematica.Litematica;
import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.partitions.litematica.LitematicaSettingsDialog;
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
        final PModMenu<Litematica> menu = new PModMenu<>("Litematica", Litematica.class);
        menu.addModIconIfPresent("litematica");

        final JMenuItem settingsMenuItem = new JMenuItem("Settings");
        settingsMenuItem.addActionListener((e) -> new LitematicaSettingsDialog().setVisible(true));
        menu.add(settingsMenuItem);

        W2KMenu.queueModMenu(menu);
    }
}
