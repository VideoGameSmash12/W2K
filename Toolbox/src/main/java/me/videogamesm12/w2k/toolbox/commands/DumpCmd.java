package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Parameters(name = "dump", usage = "/dump <entities | maps | screen | tiles>")
public class DumpCmd extends WCommand
{
	@ExecutionPath("tiles")
	public void tiles()
	{
		msg(Component.translatable("w2k.toolbox.dump.starting.tiles", NamedTextColor.GRAY));

		DumpUtil.performTileEntityDump(true).whenComplete((results, throwable) ->
		{
			if (throwable != null)
			{
				W2K.getLogger().error("Stacktrace:", throwable);
				msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
				return;
			}

			msg(Component.translatable("w2k.toolbox.dump.success.tiles",
					results.getSuccessful().isEmpty() ? NamedTextColor.RED : results.getFailed().isEmpty() ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
					Component.text(results.getSuccessful().size()), Component.text(results.getFailed().size()),
					Component.text(results.getIgnored().size())));
		});
	}

	@ExecutionPath("entities")
	public void entities()
	{
		msg(Component.translatable("w2k.toolbox.dump.starting.entities", NamedTextColor.GRAY));
		DumpUtil.performEntityDump(true).whenComplete((results, throwable) ->
		{
			if (throwable != null)
			{
				W2K.getLogger().error("Stacktrace:", throwable);
				msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
				return;
			}

			msg(Component.translatable("w2k.toolbox.dump.success.entities",
					results.getSuccessful().isEmpty() ? NamedTextColor.RED : results.getFailed().isEmpty() ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
					Component.text(results.getSuccessful().size()), Component.text(results.getFailed().size())));
		});
	}

	@ExecutionPath("maps")
	public void maps()
	{
		msg(Component.translatable("w2k.toolbox.dump.starting.maps", NamedTextColor.GRAY));
		DumpUtil.performMapDump(true).whenComplete((results, throwable) ->
		{
			if (throwable != null)
			{
				W2K.getLogger().error("Stacktrace:", throwable);
				msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
				return;
			}

			msg(Component.translatable("w2k.toolbox.dump.success.maps",
					results.getSuccessful().isEmpty() ? NamedTextColor.RED : results.getFailed().isEmpty() ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
					Component.text(results.getSuccessful().size()), Component.text(results.getFailed().size())));
		});
	}

	@ExecutionPath("screen")
	public void screen()
	{
		msg(Component.translatable("w2k.toolbox.dump.starting.screen", NamedTextColor.GRAY));
		schedule(() -> DumpUtil.performOpenInventoryDump(true).whenComplete((results, throwable) ->
		{
			if (throwable != null)
			{
				W2K.getLogger().error("Stacktrace:", throwable);
				msg(Component.translatable("w2k.toolbox.dump.error", NamedTextColor.RED));
				return;
			}

			msg(Component.translatable("w2k.toolbox.dump.success.screen",
					results.getSuccessful().isEmpty() ? NamedTextColor.RED : results.getFailed().isEmpty() ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
					Component.text(results.getSuccessful().size()), Component.text(results.getFailed().size()),
					Component.text(results.getIgnored().size())));
		}), 5000);
	}

	@Override
	public boolean executeCommand(String commandLabel, String[] args)
	{
		if (args.length == 0)
		{
			return false;
		}

		switch (args[0].toLowerCase())
		{
			case "tiles":
			{
				tiles();
				break;
			}
			case "entities":
			{
				entities();
				break;
			}
			case "maps":
			{
				maps();
				break;
			}
			case "menu":
			case "screen":
			{
				screen();
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
