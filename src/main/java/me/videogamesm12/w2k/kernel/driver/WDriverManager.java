package me.videogamesm12.w2k.kernel.driver;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.driver.base.*;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>WDriverManager</h1>
 * <p>W2K's management system for drivers. W2K uses as little Minecraft code as possible and instead opts to outsource a
 * lot of functionality to "drivers" (which are instances of {@link WDriver}). Drivers are registered using specific
 * entrypoints defined in a mod's {@code fabric.mod.json} file.</p>
 * <p>Drivers required for W2K to function on specific versions of Minecraft are as follows:</p>
 * <ul>
 *     <li>{@link WVersionBridgeDriver} ({@code w2k-version-bridge-driver}): A driver to call any Minecraft code.
 *     Implementations should never be re-used across versions unless you are <i>absolutely confident</i> that it
 *     will work and that nothing could possibly go wrong as a result of doing so.</li>
 *     <li>{@link WVersionFetcherDriver} ({@code w2k-version-fetcher-driver}): A driver to fetch the current version of
 *     the game. This usually doesn't need to be implemented yourself as a generic driver from the
 *     {@code Drivers_Generic} subproject is used.</li>
 *     <li>{@link WEventPassThruDriver} ({@code w2k-event-passthru-driver}): A driver to pass through client start and
 *     stop events from a Fabric API.</li>
 * </ul>
 * <p>Drivers marked as "required" but not necessary to start the game are as follows:</p>
 * <ul>
 *     <li>{@link WCommandDriver} ({@code w2k-command-wrapper-driver}): A driver to wrap and register client-side
 *     commands through an existing API (or creates one if present). This driver may be necessary to start the game in
 *     the future.</li>
 * </ul>
 * <p>Mods wishing to hook into W2K can do so by registering an instance of {@link WDriver} in the same way under the
 * {@code w2k-optional-driver} entrypoint.</p>
 */
@Getter
public class WDriverManager
{
    private WVersionBridgeDriver versionBridge;
    private WVersionFetcherDriver versionFetcher;
    private WEventPassThruDriver eventPassThru;
    private WCommandDriver commandWrapper;
    private final Map<String, WDriver> optionalDrivers = new HashMap<>();

    public void loadRequiredDrivers()
    {
        eventPassThru = FabricLoader.getInstance().getEntrypoints("w2k-event-passthru-driver",
                WEventPassThruDriver.class).stream().filter(WDriver::isSupported).findAny()
                .orElseThrow(() -> new IllegalStateException("Event pass-through driver not found!"));
        versionBridge = FabricLoader.getInstance().getEntrypoints("w2k-version-bridge-driver",
                WVersionBridgeDriver.class).stream().filter(WDriver::isSupported).findAny()
                .orElseThrow(() -> new IllegalStateException("Version bridge driver not found!"));
        versionFetcher = FabricLoader.getInstance().getEntrypoints("w2k-version-fetcher-driver",
                WVersionFetcherDriver.class).stream().filter(WDriver::isSupported).findAny()
                .orElseThrow(() -> new IllegalStateException("Version fetcher driver not found!"));

        commandWrapper = FabricLoader.getInstance().getEntrypoints("w2k-command-wrapper-driver",
                WCommandDriver.class).stream().filter(WDriver::isSupported).findAny().orElse(null);

        eventPassThru.setupEvents();
    }

    public void loadOptionalDrivers()
    {
        FabricLoader.getInstance().getEntrypoints("w2k-optional-driver", WDriver.class).stream().filter(WDriver::isSupported).forEach(driver ->
        {
            optionalDrivers.put(driver.getMetadata().identifier(), driver);
            driver.onInitialize();
        });
    }
}
