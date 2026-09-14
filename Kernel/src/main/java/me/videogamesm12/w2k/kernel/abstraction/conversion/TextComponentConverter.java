package me.videogamesm12.w2k.kernel.abstraction.conversion;

import com.google.gson.JsonElement;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

@RequiredArgsConstructor
public class TextComponentConverter<Text>
{
    private final Function<Text, Component> nativeToAdventure;
    private final Function<Component, Text> adventureToNative;
    private final Function<Component, String> adventureToString;

    public Component nativeToAdventure(Text text)
    {
        return nativeToAdventure.apply(text);
    }

    public Text adventureToNative(Component component)
    {
        return adventureToNative.apply(component);
    }

    public String adventureToString(Component component)
    {
        return adventureToString.apply(component);
    }
}
