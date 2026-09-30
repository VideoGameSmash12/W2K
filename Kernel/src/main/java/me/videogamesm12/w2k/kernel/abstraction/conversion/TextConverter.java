package me.videogamesm12.w2k.kernel.abstraction.conversion;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import net.kyori.adventure.text.Component;

/**
 * <h1>TextConverter</h1>
 * <p>A class containing a series of functions which allows you to convert text components between the Adventure and
 *  native Minecraft formats.</p>
 * <p>Instances of this class can be obtained by calling {@link BaseVersionAbstractionLayer#text()}.</p>
 * @param <NativeText>  {@code Text} or {@code Component} (depending on your mappings)
 */
public interface TextConverter<NativeText>
{
    /**
     * Converts a text component from the native format to the Adventure format.
     * @param text      {@code Text} or {@code Component} (depending on your mappings)
     * @return          {@link Component}
     */
    Component nativeToAdventure(NativeText text);

    /**
     * Converts a text component from the Adventure format to the native format.
     * @param component {@link Component}
     * @return          {@code Text} or {@code Component} (depending on your mappings)
     */
    NativeText adventureToNative(Component component);

    /**
     * Gets a stringified form of a text component from the Adventure format.
     * @param component {@link Component}
     * @param useNative {@code boolean} (whether to use the native serializer or the one built into Adventure)
     * @return          {@link String}
     */
    String adventureToString(Component component, boolean useNative);

    /**
     * Converts a text component from the native format to JSON.
     * @param text      {@code Text} or {@code Component} (depending on your mappings)
     * @return          {@link JsonElement}
     */
    JsonElement nativeToJson(NativeText text);

    /**
     * Gets a stringified form of a text component from a {@link JsonElement}.
     * @param component {@link JsonElement}
     * @return          {@link String}
     */
    String jsonToString(JsonElement component);
}
