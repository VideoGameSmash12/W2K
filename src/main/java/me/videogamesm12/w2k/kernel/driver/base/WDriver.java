package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface WDriver
{
    default String name()
    {
        return getClass().getName();
    }

    default WDriverMetadata getMetadata()
    {
        final Class<? extends WDriver> driverClass = getClass();
        return driverClass.isAnnotationPresent(WDriverMetadata.class) ? driverClass.getAnnotation(WDriverMetadata.class) : null;
    }

    default boolean isSupported()
    {
        final WDriverMetadata metadata = getMetadata();

        if (metadata == null)
        {
            W2K.getLogger().warn("Ignoring invalid driver {} as it is missing important data", name());
            return false;
        }

        final List<String> missingRequiredMods = Arrays.stream(metadata.requiredMods()).filter(mod -> !FabricLoader.getInstance().isModLoaded(mod)).collect(Collectors.toList());
        if (!missingRequiredMods.isEmpty())
        {
            W2K.getLogger().warn("Ignoring driver {} as the mods it depends on are missing: {}", name(), missingRequiredMods);
            return false;
        }

        final List<String> breaks = Arrays.stream(metadata.requiredMods()).filter(mod -> FabricLoader.getInstance().isModLoaded(mod)).collect(Collectors.toList());
        if (!breaks.isEmpty())
        {
            W2K.getLogger().warn("Ignoring driver {} as it breaks mods: {}", name(), breaks);
            return false;
        }

        return true;
    }

    default void onInitialize()
    {
    }
}
