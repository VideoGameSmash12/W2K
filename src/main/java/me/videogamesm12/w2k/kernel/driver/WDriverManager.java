package me.videogamesm12.w2k.kernel.driver;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.driver.base.*;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;
import java.util.Map;

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
        eventPassThru = FabricLoader.getInstance().getEntrypoints("w2k-event-passthru-driver", WEventPassThruDriver.class).stream().filter(WDriver::isSupported).findAny().orElseThrow(() -> new IllegalStateException("Event pass-through driver not found!"));
        versionBridge = FabricLoader.getInstance().getEntrypoints("w2k-version-bridge-driver", WVersionBridgeDriver.class).stream().filter(WDriver::isSupported).findAny().orElseThrow(() -> new IllegalStateException("Version bridge driver not found!"));
        versionFetcher = FabricLoader.getInstance().getEntrypoints("w2k-version-fetcher-driver", WVersionFetcherDriver.class).stream().filter(WDriver::isSupported).findAny().orElseThrow(() -> new IllegalStateException("Version fetcher driver not found!"));
        commandWrapper = FabricLoader.getInstance().getEntrypoints("w2k-command-wrapper-driver", WCommandDriver.class).stream().filter(WDriver::isSupported).findAny().orElse(null);

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
