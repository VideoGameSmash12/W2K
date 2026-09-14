package me.videogamesm12.w2k.kernel.abstraction.network;

import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface PlayerListEntryInterface extends ObjectInterface
{
    GameProfile w2k$profile();

    String w2k$username();

    UUID w2k$uuid();

    Component w2k$displayName();

    int w2k$latency();

    String w2k$gameMode();

    String w2k$model();

    String w2k$skinIdentifier();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$username(),                                     // Username
                w2k$val().text().adventureToString(w2k$displayName()),  // Display Name
                w2k$uuid(),                                         // UUID
                w2k$latency(),                                      // Ping
                w2k$gameMode(),                                     // Gamemode
                w2k$model(),                                        // Skin Model
                w2k$skinIdentifier()                                // Skin Identifier
        );
    }
}