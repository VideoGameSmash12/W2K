package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Parameters(name = "dump", usage = "/dump <entities> [parallel]")
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
			case "entities":
			{
				msg(Component.translatable("w2k.toolbox.dump.starting.entities", NamedTextColor.GRAY));
				DumpUtil.performEntityDump(parallel).whenComplete((results, throwable) ->
				{
					if (throwable != null)
					{
						W2K.getLogger().error("Stacktrace:", throwable);
						msg(Component.translatable("w2k.toolbox.dump.error"));
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
		}
		return true;
	}
}
