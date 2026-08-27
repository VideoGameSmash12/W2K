package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>WDriver</h1>
 * <p>The foundational interface for W2K's driver system.</p>
 * <p>For more information about W2K's driver system, please see the documentation for
 * {@link me.videogamesm12.w2k.kernel.driver.WDriverManager WDriverManager}.</p>
 */
public interface WDriver
{
    /**
     * Returns the name of the driver.
     * @return String
     */
    default String name()
    {
        return getClass().getName();
    }

    default WDriverMetadata getMetadata()
    {
        final Class<? extends WDriver> driverClass = getClass();
        return driverClass.isAnnotationPresent(WDriverMetadata.class) ? driverClass.getAnnotation(WDriverMetadata.class) : null;
    }

    /**
     * Use this driver's {@link WDriverMetadata metadata} to determine whether it is supported.
     * @return  True if the driver has metadata, the mods required for the driver to function are present, and that no
     *          mods that conflict according to the metadata are present.
     */
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

        final List<String> breaks = Arrays.stream(metadata.breaks()).filter(mod -> FabricLoader.getInstance().isModLoaded(mod)).collect(Collectors.toList());
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
