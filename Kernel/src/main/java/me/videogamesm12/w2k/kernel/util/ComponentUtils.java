package me.videogamesm12.w2k.kernel.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

/**
 * <h1>ComponentUtils</h1>
 * <p>Utility class for converting components between Adventure's {@link Component} and GSON's {@link JsonElement}.</p>
 */
public class ComponentUtils
{
    private static final GsonComponentSerializer MODERN_GSON_SERIALIZER = GsonComponentSerializer.gson();
    private static final GsonComponentSerializer LEGACY_GSON_SERIALIZER = GsonComponentSerializer.colorDownsamplingGson();
    private static final Gson GSON = new Gson();

    /**
     * Serialize a {@link Component} as a {@link JsonElement} formatted in a way that works with current versions of the
     *  game with all the bells and whistles.
     * @param component {@link Component}
     * @return          {@link JsonElement}
     */
    public static JsonElement serializeComponent(Component component)
    {
        return MODERN_GSON_SERIALIZER.serializeToTree(component);
    }

    /**
     * Serialize a {@link Component} as a {@link JsonElement} formatted in a way that works with older versions of the
     *  game where components are serialized differently.
     * @param component {@link Component}
     * @return          {@link JsonElement}
     */
    public static JsonElement serializeComponentAsLegacy(Component component)
    {
        return LEGACY_GSON_SERIALIZER.serializeToTree(component);
    }

    /**
     * Read a String as a {@link JsonElement}.
     * @param json  String
     * @return      {@link JsonElement}
     */
    public static JsonElement stringToElement(String json)
    {
        return GSON.fromJson(json, JsonElement.class);
    }
}
