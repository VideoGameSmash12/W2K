package me.videogamesm12.w2k.drivers.generic.required;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionFetcherDriver;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

@WDriverMetadata(identifier = "generic:version_fetcher", minVersion = "1.8.9", maxVersion = "*", minProtocolVersion = 0, maxProtocolVersion = 999)
public class WGenericVersionFetcherDriver implements WVersionFetcherDriver
{
    private static MinecraftVersion version;

    static
    {
        try (InputStream stream = FabricLoader.class.getClassLoader().getResourceAsStream("version.json"))
        {
            version = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(stream)), MinecraftVersion.class);
        }
        catch (NullPointerException | IOException ignored)
        {
            version = new MinecraftVersion(MinecraftClient.getInstance().getGameVersion(), MinecraftClient.getInstance().getGameVersion());
        }
    }

    @Override
    public String getGameVersion()
    {
        return version.getId();
    }

    @AllArgsConstructor
    @Data
    public static class MinecraftVersion
    {
        private String id;

        private String name;
    }
}
