package me.videogamesm12.w2k.drivers.v1_8.required;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.driver.base.WTextDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;

public class W18TextDriver implements WTextDriver<Text>
{
    @Override
    public Text fromJson(JsonElement element)
    {
        return Text.Serializer.deserialize(element.toString());
    }

    @Override
    public Text fromComponent(Component component)
    {
        return fromJson(ComponentUtils.serializeComponentAsLegacy(component));
    }
}
