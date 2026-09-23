package me.videogamesm12.w2k.kernel.driver;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.*;

/**
 * <h1>WDriverManager</h1>
 * <p>W2K's management system for drivers. W2K tries to avoid calling or hooking into code belonging to other mods in
 *  and instead opts to outsource code that hook mods into W2K into what are called drivers. Drivers are registered
 *  using the {@code w2k-driver} entrypoint defined in a mod's {@code fabric.mod.json} file.</p>
 */
@Getter
public class WDriverManager
{
    private final Multimap<ModContainer, Driver> drivers = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(MultimapBuilder.hashKeys()).arrayListValues()).build());

    public void loadDrivers()
    {
        try
        {
            FabricLoader.getInstance().getEntrypointContainers("w2k-driver", Driver.class).forEach(container ->
                    drivers.put(container.getProvider(), container.getEntrypoint().mod(container.getProvider())));
        }
        catch (Throwable ex)
        {
            W2K.getLogger().error("Failed to load driver, aborting loading process", ex);
        }
    }

    public Map<ModContainer, Collection<Driver>> driversByMod()
    {
        return drivers.asMap();
    }
}
