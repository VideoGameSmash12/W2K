package me.videogamesm12.w2k.kernel.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class ComponentUtils
{
    private static final GsonComponentSerializer MODERN_GSON_SERIALIZER = GsonComponentSerializer.gson();
    private static final GsonComponentSerializer LEGACY_GSON_SERIALIZER = GsonComponentSerializer.colorDownsamplingGson();
    private static final Gson GSON = new Gson();

    public static JsonElement serializeComponent(Component component)
    {
        return MODERN_GSON_SERIALIZER.serializeToTree(component);
    }

    public static JsonElement serializeComponentAsLegacy(Component component)
    {
        return LEGACY_GSON_SERIALIZER.serializeToTree(component);
    }

    public static JsonElement stringToElement(String json)
    {
        return GSON.fromJson(json, JsonElement.class);
    }
}
