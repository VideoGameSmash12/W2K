package me.videogamesm12.w2k.kernel.wrapper.network;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface WrappedPlayerListEntry
{
	String w2k$getPlayerName();

	@Nullable UUID w2k$getPlayerUuid();

	JsonElement w2k$getDisplayName();

	int w2k$getLatency();

	String w2k$getGameMode();

	String w2k$getModel();

	String w2k$getSkinIdentifier();
}
