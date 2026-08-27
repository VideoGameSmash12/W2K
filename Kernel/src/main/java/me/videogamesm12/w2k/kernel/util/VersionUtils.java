package me.videogamesm12.w2k.kernel.util;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * <h1>VersionUtils</h1>
 * <p>Utility class for comparing Minecraft versions with the one being used on runtime.</p>
 */
public class VersionUtils
{
    @Getter
    private static MinecraftVersion gameVersion;

    static
    {
        try (InputStream stream = FabricLoader.class.getClassLoader().getResourceAsStream("version.json"))
        {
            gameVersion = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(stream)), MinecraftVersion.class);
        }
        catch (NullPointerException | IOException ignored)
        {
            gameVersion = new MinecraftVersion("0.0.0", "0.0.0");
        }
    }

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
            currentVersion = SemanticVersion.parse(gameVersion.getId());
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
            currentVersion = SemanticVersion.parse(gameVersion.getId());
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

    @AllArgsConstructor
    @Data
    public static class MinecraftVersion
    {
        private String id;

        private String name;
    }
}
