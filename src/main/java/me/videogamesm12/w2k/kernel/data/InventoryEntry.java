package me.videogamesm12.w2k.kernel.data;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class InventoryEntry
{
    private final JsonElement name;

    private final String type;

    private final int count;

    private final int damage;

    private final String location;

    private final String data;

    public List<Object> toTableRow()
    {
        return Arrays.asList(
                name != null ?
                        W2K.getInstance().getDriverManager().getVersionBridge().textToString(name) : null,  // Display Name
                type,                                                                                       // ID
                count,                                                                                      // Count
                damage,                                                                                     // Damage
                location,                                                                                   // Location
                data                                                                                        // Data
        );
    }

    public boolean isNotEmpty()
    {
        return !type.equalsIgnoreCase("minecraft:air");
    }
}