package me.videogamesm12.w2k.kernel.data;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * <h1>EntityEntry</h1>
 * <p>Version-agnostic wrapper for Entity.</p>
 */
@RequiredArgsConstructor
@Getter
public class EntityEntry
{
    private final JsonElement name;

    private final String type;

    private final String location;

    private final int id;

    private final UUID uuid;

    private final String nbt;

    public List<Object> toTableRow()
    {
        return Arrays.asList(
                name != null ?
                        W2K.getInstance().getDriverManager().getVersionBridge().textToString(name) : null,  // Display Name
                type,                                                                                       // Type
                location,                                                                                   // Location
                id,                                                                                         // ID
                uuid.toString(),                                                                            // UUID
                nbt                                                                                         // NBT Data
        );
    }
}
