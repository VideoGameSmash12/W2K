package me.videogamesm12.w2k.kernel.util;

import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

/**
 * <h1>VersionUtils</h1>
 * <p>Utility class for comparing Minecraft versions with the one being used on runtime.</p>
 */
public class VersionUtils
{
    /**
     * Checks whether the currently running version of Minecraft is newer than or equal to a provided version string.
     * @param versionString String
     * @return              True if the version of Minecraft running is newer than or equal to the provided version
     */
    public static boolean isNewerThanOrRunning(String versionString)
    {
        final SemanticVersion currentVersion;
        try
        {
            currentVersion = SemanticVersion.parse(W2K.getInstance().getDriverManager().getVersionFetcher().getGameVersion());
        }
        catch (VersionParsingException ex)
        {
            W2K.getLogger().error("Failed to read current version", ex);
            return false;
        }
        final SemanticVersion version;
        try
        {
            version = SemanticVersion.parse(versionString);
        }
        catch (VersionParsingException ex)
        {
            W2K.getLogger().error("Failed to read version string", ex);
            return false;
        }

        return currentVersion.compareTo((Version) version) >= 0;
    }

    /**
     * Checks whether the currently running version of Minecraft is older than or equal to a provided version string.
     * @param versionString String
     * @return              True if the version of Minecraft running is older than or equal to the provided version
     */
    public static boolean isOlderThanOrRunning(String versionString)
    {
        final SemanticVersion currentVersion;
        try
        {
            currentVersion = SemanticVersion.parse(W2K.getInstance().getDriverManager().getVersionFetcher().getGameVersion());
        }
        catch (VersionParsingException ex)
        {
            W2K.getLogger().error("Failed to read current version", ex);
            return false;
        }
        final SemanticVersion version;
        try
        {
            version = SemanticVersion.parse(versionString);
        }
        catch (VersionParsingException ex)
        {
            W2K.getLogger().error("Failed to read version string", ex);
            return false;
        }

        return currentVersion.compareTo((Version) version) <= 0;
    }
}
