package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;

@Parameters(name = "test", usage = "test")
public class TestCmd extends WCommand
{
	@Override
	public boolean executeCommand(String commandLabel, String[] args)
	{
		W2K.getInstance().getDriverManager().getVersionBridge().getNearbyEntitiesExperimental().forEach(entry ->
		{
			System.out.println(entry.w2kGetEntityLocation());
		});
		return true;
	}
}
