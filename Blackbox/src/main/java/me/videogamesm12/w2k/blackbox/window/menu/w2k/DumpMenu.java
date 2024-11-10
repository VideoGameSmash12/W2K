package me.videogamesm12.w2k.blackbox.window.menu.w2k;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;

import javax.swing.*;
import java.io.File;

public class DumpMenu extends JMenu
{
	public DumpMenu()
	{
		super("Dump");

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

			String[] complete = (String[]) results[0];
			String[] failed = (String[]) results[1];
			String[] discarded = (String[]) results[2];
			File dumpFolder = (File) results[3];

			SwingUtilities.invokeLater(() ->
			{
				int prompt = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
						String.format("Screen dump complete (%d successful, %d failed, %d ignored). Would you like to view it?", complete.length, failed.length, discarded.length),
						"Dump completed", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);

				if (prompt == JOptionPane.YES_OPTION)
				{
					String[] fileViewerCommand = {"xdg-open", dumpFolder.getAbsolutePath()};

					if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.WINDOWS)
					{
						fileViewerCommand = new String[] {"rundll32", "url.dll,FileProtocolHandler", dumpFolder.getAbsolutePath()};
					}
					else if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.MAC_OS)
					{
						fileViewerCommand = new String[] {"open", dumpFolder.getAbsolutePath()};
					}

					try
					{
						SysUtils.execute(fileViewerCommand);
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

			String[] complete = (String[]) results[0];
			String[] failed = (String[]) results[1];
			String[] discarded = (String[]) results[2];
			File dumpFolder = (File) results[3];

			SwingUtilities.invokeLater(() ->
			{
				int prompt = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
						String.format("Tile entity dump complete (%d successful, %d failed, %d ignored). Would you like to view it?", complete.length, failed.length, discarded.length),
						"Dump completed", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);

				if (prompt == JOptionPane.YES_OPTION)
				{
					String[] fileViewerCommand = {"xdg-open", dumpFolder.getAbsolutePath()};

					if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.WINDOWS)
					{
						fileViewerCommand = new String[] {"rundll32", "url.dll,FileProtocolHandler", dumpFolder.getAbsolutePath()};
					}
					else if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.MAC_OS)
					{
						fileViewerCommand = new String[] {"open", dumpFolder.getAbsolutePath()};
					}

					try
					{
						SysUtils.execute(fileViewerCommand);
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

			String[] complete = (String[]) results[0];
			String[] failed = (String[]) results[1];

			SwingUtilities.invokeLater(() ->
			{
				int prompt = JOptionPane.showConfirmDialog(Blackbox.getInstance().getMainWindow(),
						String.format("Entity dump complete (%d successful, %d failed). Would you like to view it?", complete.length, failed.length),
						"Dump completed", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);

				if (prompt == JOptionPane.YES_OPTION)
				{
					File folderPath = (File) results[3][0];
					String[] fileViewerCommand = {"xdg-open", folderPath.getAbsolutePath()};

					if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.WINDOWS)
					{
						fileViewerCommand = new String[] {"rundll32", "url.dll,FileProtocolHandler", folderPath.getAbsolutePath()};
					}
					else if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.MAC_OS)
					{
						fileViewerCommand = new String[] {"open", folderPath.getAbsolutePath()};
					}

					try
					{
						SysUtils.execute(fileViewerCommand);
					}
					catch (Throwable ignored)
					{
					}
				}
			});
		}));
		add(dumpEntities);
	}
}
