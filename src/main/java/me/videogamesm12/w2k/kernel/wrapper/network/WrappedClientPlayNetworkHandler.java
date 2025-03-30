package me.videogamesm12.w2k.kernel.wrapper.network;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface WrappedClientPlayNetworkHandler
{
	List<WrappedPlayerListEntry> w2k$getPlayerList();

	void w2k$disconnect(Component reason);
}
