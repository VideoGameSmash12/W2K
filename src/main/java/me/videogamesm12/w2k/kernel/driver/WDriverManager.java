package me.videogamesm12.w2k.kernel.driver;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.driver.base.WVersionFetcherDriver;
import net.fabricmc.loader.api.FabricLoader;

@Getter
public class WDriverManager
{
    private WVersionFetcherDriver versionFetcher;

    public void loadRequiredDrivers()
    {
        versionFetcher = FabricLoader.getInstance().getEntrypoints("w2k-version-fetcher-driver", WVersionFetcherDriver.class).stream().findAny().orElseThrow(() -> new IllegalStateException("Version fetcher driver not found!"));
    }
}
