package me.videogamesm12.w2k.kernel.abstraction.network;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <h1>PlayerListEntryInterface</h1>
 * <p>A wrapper interface for {@code PlayerListEntry} or {@code PlayerInfo} (depending on your mappings) instances
 *  implemented using Mixins.</p>
 */
public interface PlayerListEntryInterface extends ObjectInterface
{
    /**
     * Gets the {@link GameProfileInterface GameProfile} associated with this entry.
     * @return  {@link GameProfileInterface}
     */
    GameProfileInterface w2k$profile();

    /**
     * Gets the player's username.
     * @return  {@link String}
     */
    String w2k$username();

    /**
     * Gets the player's UUID.
     * @return  {@link UUID}
     */
    UUID w2k$uuid();

    /**
     * Gets the player's tab display name.
     * @return  {@link JsonElement}
     */
    JsonElement w2k$displayName();

    /**
     * Gets the player's ping.
     * @return  {@code int}
     */
    int w2k$latency();

    /**
     * Gets the player's game mode.
     * @return  {@link String}
     */
    String w2k$gameMode();

    /**
     * Gets the player's skin model.
     * @return  {@link String}
     */
    String w2k$model();

    /**
     * Gets the internal identifier for the player's skin.
     * @return  {@link String}
     */
    String w2k$skinIdentifier();

    /**
     * <p>Creates a list of data to be displayed in components as a table row.</p>
     * <p>The data listed is as follows:</p>
     * <ul>
     *     <li>Username (fetched using {@link w2k$username()})</li>
     *     <li>Display Name (fetched using {@link w2k$displayName()})</li>
     *     <li>UUID (fetched using {@link w2k$uuid()})</li>
     *     <li>Ping (fetched using {@link w2k$latency()})</li>
     *     <li>Game Mode (fetched using {@link w2k$gameMode()})</li>
     *     <li>Skin Model (fetched using {@link w2k$model()})</li>
     *     <li>Skin Identifier (fetched using {@link w2k$skinIdentifier()})</li>
     * </ul>
     * @return  {@link java.util.List}
     */
    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$username(),                                     // Username
                w2k$val().text().jsonToString(w2k$displayName()),   // Display Name
                w2k$uuid(),                                         // UUID
                w2k$latency(),                                      // Ping
                w2k$gameMode(),                                     // Gamemode
                w2k$model(),                                        // Skin Model
                w2k$skinIdentifier()                                // Skin Identifier
        );
    }
}