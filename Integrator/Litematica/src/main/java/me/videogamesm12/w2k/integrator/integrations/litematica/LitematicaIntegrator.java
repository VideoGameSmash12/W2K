package me.videogamesm12.w2k.integrator.integrations.litematica;

import fi.dy.masa.litematica.Litematica;
import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.IModIntegrator;
import me.videogamesm12.w2k.integrator.core.IntegratorMetadata;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.integrations.litematica.menu.LitematicaSettingsDialog;

import javax.swing.*;

@IntegratorMetadata(required = "litematica")
public class LitematicaIntegrator extends IModIntegrator
{
	@Override
	public void onStart()
	{
		final PModMenu<Litematica> menu = new PModMenu<>("Litematica", Litematica.class);
		menu.addModIconIfPresent("litematica");

		final JMenuItem settingsMenuItem = new JMenuItem("Settings");
		settingsMenuItem.addActionListener((e) -> new LitematicaSettingsDialog().setVisible(true));
		menu.add(settingsMenuItem);

		W2KMenu.queueModMenu(menu);
	}
}
