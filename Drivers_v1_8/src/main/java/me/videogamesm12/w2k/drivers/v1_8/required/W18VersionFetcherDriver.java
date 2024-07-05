package me.videogamesm12.w2k.drivers.v1_8.required;

import me.videogamesm12.w2k.kernel.driver.base.WVersionFetcherDriver;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;

public class W18VersionFetcherDriver implements WVersionFetcherDriver
{
    @Override
    public String getGameVersion()
    {
        return MinecraftClient.getInstance().getGameVersion();
    }

    @Override
    public String getClientBrand()
    {
        return ClientBrandRetriever.getClientModName();
    }
}
