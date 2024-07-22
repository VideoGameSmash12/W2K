package me.videogamesm12.w2k.kernel.data;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>PlayerEntry</h1>
 * <p>Version-agnostic wrapper for PlayerListEntry.</p>
 */
@RequiredArgsConstructor
@Getter
public class PlayerEntry
{
    private final GameProfile profile;

    private final JsonElement displayName;

    private final int latency;

    private final String gameMode;

    private final String model;

    private final String skinIdentifier;

    public List<Object> toTableRow()
    {
        return Arrays.asList(
                profile.getName(),                                                                                      // Username
                displayName != null ?
                        W2K.getInstance().getDriverManager().getVersionBridge().textToString(displayName) : null,       // Display Name
                profile.getId(),                                                                                        // UUID
                latency,                                                                                                // Ping
                gameMode,                                                                                               // Gamemode
                model,                                                                                                  // Skin Model
                skinIdentifier                                                                                          // Skin Identifier
        );
    }
}
