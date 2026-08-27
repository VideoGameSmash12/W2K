package me.videogamesm12.w2k.kernel.driver.base;

/**
 * <h1>WVersionFetcherDriver</h1>
 * <p>A specific type of WDriver that fetches the current version of Minecraft.</p>
 */
public interface WVersionFetcherDriver extends WDriver
{
    String getGameVersion();
}
