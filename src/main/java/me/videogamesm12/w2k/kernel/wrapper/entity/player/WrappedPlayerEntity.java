package me.videogamesm12.w2k.kernel.wrapper.entity.player;

import net.kyori.adventure.text.Component;

public interface WrappedPlayerEntity
{
	void w2k$requestRespawn();

	void w2k$displayMessage(Component component, boolean actionBar);

	void w2k$sendMessage(String message);

	void w2k$runCommand(String message);
}
