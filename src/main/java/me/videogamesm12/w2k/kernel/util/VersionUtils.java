package me.videogamesm12.w2k.kernel.util;

import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

public class VersionUtils
{
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
