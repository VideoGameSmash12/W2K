package me.videogamesm12.w2k.kernel.abstraction.conversion;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;

public interface TextConverter<NativeText>
{
    Component nativeToAdventure(NativeText text);

    NativeText adventureToNative(Component component);

    String adventureToString(Component component, boolean useNative);

    String jsonToString(JsonElement component);
}
