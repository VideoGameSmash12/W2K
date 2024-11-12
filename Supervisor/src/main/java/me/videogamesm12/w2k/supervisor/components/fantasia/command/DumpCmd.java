package me.videogamesm12.w2k.supervisor.components.fantasia.command;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.CommandSender;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;

public class DumpCmd extends FCommand
{
	public DumpCmd()
	{
		super("dump", "Dump various types of data in memory to disk.", "dump <entities | maps | screen | tiles> [parallel]");
	}

	@Override
	public boolean run(CommandSender sender, String[] args)
	{
		if (!Supervisor.getInstance().getFlags().isGameStartedYet())
		{
			sender.sendMessage("The game hasn't started yet.");
			return true;
		}

		if (args.length == 0)
		{
			return false;
		}

		boolean parallel = args.length > 1 && args[1].equalsIgnoreCase("parallel");

		switch (args[0].toLowerCase())
		{
			case "entities":
			{
				sender.sendMessage("Dumping entities...");
				DumpUtil.performEntityDump(parallel).whenComplete((result, exception) ->
				{
					if (exception != null)
					{
						sender.sendMessage("An unrecoverable error occurred during the dump. Check the client logs for more information.");
						W2K.getLogger().error("Stacktrace:", exception);
						return;
					}

					sender.sendMessage(String.format("Entity dump complete (%d successful, %d failed).",
							result.getSuccessful().size(), result.getFailed().size()));
					sender.sendMessage("The dump is located at " + result.getOutputDirectory().getAbsolutePath());
				});
				break;
			}
			case "maps":
			{
				sender.sendMessage("Dumping maps...");
				DumpUtil.performMapDump(parallel).whenComplete((result, exception) ->
				{
					if (exception != null)
					{
						sender.sendMessage("An unrecoverable error occurred during the dump. Check the client logs for more information.");
						W2K.getLogger().error("Stacktrace:", exception);
						return;
					}

					sender.sendMessage(String.format("Map dump complete (%d successful, %d failed).",
							result.getSuccessful().size(), result.getFailed().size()));
					sender.sendMessage("The dump is located at " + result.getOutputDirectory().getAbsolutePath());
				});
				break;
			}
			case "menu":
			case "screen":
			{
				sender.sendMessage("Dumping open screen (if one is open)...");
				DumpUtil.performOpenInventoryDump(parallel).whenComplete((result, exception) ->
				{
					if (exception != null)
					{
						sender.sendMessage("An unrecoverable error occurred during the dump. Check the client logs for more information.");
						W2K.getLogger().error("Stacktrace:", exception);
						return;
					}

					sender.sendMessage(String.format("Inventory dump complete (%d successful, %d failed, %d ignored).",
							result.getSuccessful().size(), result.getFailed().size(), result.getIgnored().size()));
					sender.sendMessage("The dump is located at " + result.getOutputDirectory().getAbsolutePath());
				});
				break;
			}
			case "tiles":
			{
				DumpUtil.performTileEntityDump(parallel).whenComplete((result, exception) ->
				{
					if (exception != null)
					{
						sender.sendMessage("An unrecoverable error occurred during the dump. Check the client logs for more information.");
						W2K.getLogger().error("Stacktrace:", exception);
						return;
					}

					sender.sendMessage(String.format("Tile entity dump complete (%d successful, %d failed, %d ignored).",
							result.getSuccessful().size(), result.getFailed().size(), result.getIgnored().size()));
					sender.sendMessage("The dump is located at " + result.getOutputDirectory().getAbsolutePath());
				});
				break;
			}
			default:
			{
				return false;
			}
		}

		return true;
	}
}
