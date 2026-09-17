package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.data.action.AbstractAction;
import net.fabricmc.loader.api.ModContainer;

import java.util.Map;

public interface Driver
{
    Map<String, AbstractAction<?>> actions();

    ModContainer mod();

    class Meta
    {
        String name;

        String[] dependencies;
    }
}
