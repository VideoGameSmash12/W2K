package me.videogamesm12.w2k.blackbox.window.menu.w2k;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;

import javax.swing.*;

public class DumpMenu extends JMenu
{
	public DumpMenu()
	{
		super("Dump");

		final JMenuItem dumpEntities = new JMenuItem("Dump nearby entities to disk");
		dumpEntities.addActionListener((e) ->
		{
			DumpUtil.performEntityDump(true).whenComplete((results, throwable) ->
			{
				if (throwable != null)
				{
					W2K.getLogger().error("Stacktrace:", throwable);
					SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(),
							"An unrecoverable error occurred during the dump. Please check the logs for more "
									+ "information.", "Dump failed", JOptionPane.ERROR_MESSAGE));
					return;
				}

				String[] complete = results[0];
				String[] failed = results[1];

				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(),
						String.format("Entity dump complete (%d successful, %d failed).", complete.length, failed.length),
						"Dump completed", JOptionPane.INFORMATION_MESSAGE));
			});
		});
		add(dumpEntities);
	}
}
