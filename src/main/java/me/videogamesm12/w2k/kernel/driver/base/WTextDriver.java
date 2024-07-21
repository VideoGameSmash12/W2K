package me.videogamesm12.w2k.kernel.driver.base;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;

public interface WTextDriver<T> extends WDriver
{
    T fromJson(JsonElement element);

    T fromComponent(Component component);
}