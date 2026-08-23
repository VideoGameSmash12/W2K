package me.videogamesm12.w2k.blackbox.window.menu.w2k;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;

import javax.swing.*;

public class DumpMenu extends JMenu
{
	public DumpMenu()
	{
		super("Dump");

		// TODO: Try to reduce the amount of boilerplate code here

		final JMenuItem dumpMaps = new JMenuItem("Dump loaded maps to disk");
		dumpMaps.addActionListener((e) -> DumpUtil.performMapDump(true).whenComplete((results, throwable) ->
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
						String.format("Map dump complete (%d successful, %d failed). Would you like to view it?",
								results.getSuccessful().size(), results.getFailed().size()),
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
		}));
		add(dumpMaps);

		final JMenuItem dumpOpenInventory = new JMenuItem("Dump items in currently open screen to disk");
		dumpOpenInventory.addActionListener((e) -> DumpUtil.performOpenInventoryDump(true).whenComplete((results, throwable) ->
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
						String.format("Screen dump complete (%d successful, %d failed, %d ignored). Would you like to view it?",
								results.getSuccessful().size(), results.getFailed().size(), results.getIgnored().size()),
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
		}));
		add(dumpOpenInventory);

		final JMenuItem dumpTileEntities = new JMenuItem("Dump nearby tile entities to disk");
		dumpTileEntities.addActionListener((e) -> DumpUtil.performTileEntityDump(true).whenComplete((results, throwable) ->
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
						String.format("Tile entity dump complete (%d successful, %d failed, %d ignored). Would you like to view it?",
								results.getSuccessful().size(), results.getFailed().size(), results.getIgnored().size()),
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
		}));
		add(dumpTileEntities);

		final JMenuItem dumpEntities = new JMenuItem("Dump nearby entities to disk");
		dumpEntities.addActionListener((e) -> DumpUtil.performEntityDump(true).whenComplete((results, throwable) ->
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
						String.format("Entity dump complete (%d successful, %d failed). Would you like to view it?",
								results.getSuccessful().size(), results.getFailed().size()),
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
		}));
		add(dumpEntities);

		addSeparator();

		final JMenuItem openDumpFolder = new JMenuItem("Browse dump folder");
		openDumpFolder.addActionListener(e -> SysUtils.getOperatingSystem().openFolder(DumpUtil.getDumpsFolder()));
		add(openDumpFolder);
	}
}
