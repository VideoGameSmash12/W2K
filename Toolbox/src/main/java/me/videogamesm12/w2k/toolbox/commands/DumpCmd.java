package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Parameters(name = "dump", usage = "/dump <entities | maps | screen | tiles> [parallel]")
public class DumpCmd extends WCommand
{
	@Override
	public boolean executeCommand(String commandLabel, String[] args)
	{
		if (args.length == 0)
		{
			return false;
		}

		boolean parallel = args.length > 1 && args[1].equalsIgnoreCase("parallel");

		switch (args[0].toLowerCase())
		{
			case "tiles":
			{
				msg(Component.translatable("w2k.toolbox.dump.starting.tiles", NamedTextColor.GRAY));
				DumpUtil.performTileEntityDump(parallel).whenComplete((results, throwable) ->
				{
					if (throwable != null)
					{
						W2K.getLogger().error("Stacktrace:", throwable);
						msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
						return;
					}

					String[] complete = (String[]) results[0];
					String[] failed = (String[]) results[1];
					String[] ignored = (String[]) results[2];

					msg(Component.translatable("w2k.toolbox.dump.success.tiles",
							complete.length == 0 ? NamedTextColor.RED : failed.length == 0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
							Component.text(complete.length), Component.text(failed.length), Component.text(ignored.length)));
				});
				break;
			}
			case "entities":
			{
				msg(Component.translatable("w2k.toolbox.dump.starting.entities", NamedTextColor.GRAY));
				DumpUtil.performEntityDump(parallel).whenComplete((results, throwable) ->
				{
					if (throwable != null)
					{
						W2K.getLogger().error("Stacktrace:", throwable);
						msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
						return;
					}

					String[] complete = (String[]) results[0];
					String[] failed = (String[]) results[1];

					msg(Component.translatable("w2k.toolbox.dump.success.entities",
							complete.length == 0 ? NamedTextColor.RED : failed.length == 0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
							Component.text(complete.length), Component.text(failed.length)));
				});
				break;
			}
			case "maps":
			{
				msg(Component.translatable("w2k.toolbox.dump.starting.maps", NamedTextColor.GRAY));
				DumpUtil.performMapDump(parallel).whenComplete((results, throwable) ->
				{
					if (throwable != null)
					{
						W2K.getLogger().error("Stacktrace:", throwable);
						msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
						return;
					}

					String[] complete = (String[]) results[0];
					String[] failed = (String[]) results[1];

					msg(Component.translatable("w2k.toolbox.dump.success.maps",
							complete.length == 0 ? NamedTextColor.RED : failed.length == 0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
							Component.text(complete.length), Component.text(failed.length)));
				});
				break;
			}
			case "menu":
			case "screen":
			{
				msg(Component.translatable("w2k.toolbox.dump.starting.screen", NamedTextColor.GRAY));
				schedule(() -> DumpUtil.performOpenInventoryDump(parallel).whenComplete((results, throwable) ->
				{
					if (throwable != null)
					{
						W2K.getLogger().error("Stacktrace:", throwable);
						msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
						return;
					}

					String[] complete = (String[]) results[0];
					String[] failed = (String[]) results[1];
					String[] ignored = (String[]) results[2];

					msg(Component.translatable("w2k.toolbox.dump.success.screen",
							complete.length == 0 ? NamedTextColor.RED : failed.length == 0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
							Component.text(complete.length), Component.text(failed.length), Component.text(ignored.length)));
				}), 5000);
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
