package me.videogamesm12.w2k.kernel.driver;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.driver.base.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>WDriverManager</h1>
 * <p>W2K's management system for drivers. W2K uses as little Minecraft code as possible and instead opts to outsource a
 * lot of functionality to "drivers" (which are instances of {@link WDriver}). Drivers are registered using specific
 * entrypoints defined in a mod's {@code fabric.mod.json} file.</p>
 * <p>Mods wishing to hook into W2K can do so by registering an instance of {@link WDriver} in the same way under the
 * {@code w2k-optional-driver} entrypoint.</p>
 */
@Getter
public class WDriverManager
{
    private final Map<String, WDriver> drivers = new HashMap<>();

    public void loadDrivers()
    {
        FabricLoader.getInstance().getEntrypoints("w2k-optional-driver", WDriver.class).stream().filter(WDriver::isSupported).forEach(driver ->
        {
            drivers.put(driver.getMetadata().identifier(), driver);
            driver.onInitialize();
        });
    }
}
