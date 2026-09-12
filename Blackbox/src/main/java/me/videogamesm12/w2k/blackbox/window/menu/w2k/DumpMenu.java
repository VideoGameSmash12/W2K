package me.videogamesm12.w2k.blackbox.window.menu.w2k;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.util.JComponents;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.toolbox.data.DumpResult;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DumpMenu extends JMenu
{
	public DumpMenu()
	{
		super("Dump");

		add(JComponents.createMenuItem("Dump loaded maps to disk",
				"Instructs the client to write all map data in its memory to disk.",
				createForDump("Map dump complete (%d successful, %d failed). Would you like to view it?",
						() -> DumpUtil.performMapDump(true))));

		add(JComponents.createMenuItem("Dump items in currently open screen to disk",
				"Instructs the client to write all items present in the currently open chest/window to disk.",
				createForDump("Screen dump complete (%d successful, %d failed, %d ignored). Would you like to view it?",
						() -> DumpUtil.performOpenInventoryDump(true))));

		add(JComponents.createMenuItem("Dump all tile entities in memory to disk",
				"Instructs the client to write all tile entity data (e.g. signs) in memory to disk.",
				createForDump("Tile entity dump complete (%d successful, %d failed, %d ignored). Would you like to view it?",
						() -> DumpUtil.performTileEntityDump(true))));

		add(JComponents.createMenuItem("Dump all entities in memory to disk",
				"Instructs the client to write all entity data in memory to disk.",
				createForDump("Entity dump complete (%d successful, %d failed). Would you like to view it?",
						() -> DumpUtil.performEntityDump(true))));

		add(JComponents.createMenuItem("Generate heap dump",
				"Instructs the JVM to generate a heap dump. Useful for diagnosing memory leaks.",
				() -> DumpUtil.generateHeapDump(false).whenComplete((result, throwable) ->
				{
					if (throwable != null)
					{
						W2K.getLogger().error("Stacktrace:", throwable);
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(),
								"An unrecoverable error occurred during the dump. Please check the logs for more "
										+ "information.", "Dump failed", JOptionPane.ERROR_MESSAGE));
						return;
					}

					SwingUtilities.invokeLater(() ->
					{
						int prompt = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
								"Heap dump completed. Would you like to open the folder it's in?",
								"Dump completed", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);

						if (prompt == JOptionPane.YES_OPTION)
						{
							try
							{
								SysUtils.getOperatingSystem().openFolder(DumpUtil.getDumpsFolder());
							}
							catch (Throwable ignored)
							{
							}
						}
					});
				})));

		addSeparator();

		add(JComponents.createMenuItem("Browse dump folder",
				"Open the dumps folder on your system.",
				() -> SysUtils.getOperatingSystem().openFolder(DumpUtil.getDumpsFolder())));
	}

	public final Runnable createForDump(final String dumpMessage,
										final Supplier<CompletableFuture<DumpResult>> futureSupplier)
	{
		return () -> futureSupplier.get().whenComplete((results, throwable) ->
		{
			if (throwable != null)
			{
				W2K.getLogger().error("Stacktrace:", throwable);
				SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(),
						"An unrecoverable error occurred during the dump. Please check the logs for more "
								+ "information.", "Dump failed", JOptionPane.ERROR_MESSAGE));
				return;
			}

			SwingUtilities.invokeLater(() ->
			{
				int prompt = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
						String.format(dumpMessage, results.getSuccessful().size(),
								results.getFailed().size(),
								results.getIgnored().size()),
						"Dump completed", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);

				if (prompt == JOptionPane.YES_OPTION)
				{
					try
					{
						SysUtils.getOperatingSystem().openFolder(results.getOutputDirectory());
					}
					catch (Throwable ignored)
					{
					}
				}
			});
		});
	}
}
