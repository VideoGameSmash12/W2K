package me.videogamesm12.w2k.kernel.driver.base;

public interface WVersionFetcherDriver extends WDriver
{
    String getGameVersion();

    String getClientBrand();
}
