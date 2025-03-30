package me.videogamesm12.w2k.kernel.wrapper.network;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface WrappedPlayerListEntry
{
	/**
	 * Gets the username of the player this entry is tied to.
	 * @return	The username as a string
	 */
	String w2k$getPlayerName();

	/**
	 * Gets this entry's universally unique identifier (UUID). Older versions of the game (pre-1.8) don't have these, so
	 * 	when running those versions this will return null
	 * @return	The entry's UUID, or null if this version does not support it
	 */
	@Nullable UUID w2k$getPlayerUuid();

	/**
	 * Gets this entry's display name. Older versions of the game (pre-1.8) don't have these as the text component
	 * 	system was still in its infancy in versions 1.7.10 and below, so when running those versions this will just
	 * 	emulate how the scoreboard system presents players in teams.
	 * @return	The display name represented as a text component.
	 */
	JsonElement w2k$getDisplayName();

	/**
	 * Gets this entry's ping as an integer.
	 * @return	The entry's ping
	 */
	int w2k$getLatency();

	/**
	 * Gets this entry's gamemode. Older versions of the game (pre-1.8) don't transmit this information, so when running
	 * 	those versions this will simply return "unknown".
	 * @return	The gamemode, or "unknown" if running a version before 1.8.
	 */
	String w2k$getGameMode();

	/**
	 * Gets this entry's set skin model. Older versions of the game (pre-1.8) didn't have different skin models, so when
	 * 	running those versions this will simply return "default".
	 * @return	The entry's skin model, or "default" if running a version before 1.8.
	 */
	String w2k$getModel();

	/**
	 * Gets this entry's set skin identifier. Older versions of the game (pre-1.8) didn't
	 * @return
	 */
	String w2k$getSkinIdentifier();
}
