package me.videogamesm12.w2k.kernel.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h1>SysUtils</h1>
 * <p>Utility class for interacting with the user's system directly.</p>
 */
public class SysUtils
{
    /**
     * Gets the current operating system.
     * @return  An instance of {@link OperatingSystem} based on the user's current configuration.
     */
    public static OperatingSystem getOperatingSystem()
    {
        final String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) return OperatingSystem.WINDOWS;
        else if (os.contains("mac")) return OperatingSystem.MAC_OS;
        else if (os.contains("linux") || os.contains("unix")) return OperatingSystem.LINUX;
        else if (os.contains("sunos")) return OperatingSystem.SOLARIS;
        else return OperatingSystem.OTHER;
    }

    /**
     * Gets the current operating system version.
     * @return  An instance of {@link OperatingSystemVersion} based on the user's current configuration.
     */
    public static OperatingSystemVersion getOperatingSystemVersion()
    {
        return OperatingSystemVersion.fromString(System.getProperty("os.version").toLowerCase());
    }

    /**
     * Executes a command on the system.
     * @param args          A series of strings to use for the command.
     * @return              {@link Process}
     * @throws IOException  If an IO exception occurs while executing the process.
     */
    public static Process execute(String... args) throws IOException
    {
        return Runtime.getRuntime().exec(args);
    }

    /**
     * Returns whether the user is currently using the Wayland Window Manager.
     * @return  True if the {@code XDG_SESSION_TYPE} environment variable is set to "wayland".
     */
    public static boolean isUsingWayland()
    {
        return System.getenv("XDG_SESSION_TYPE").equalsIgnoreCase("wayland");
    }

    @RequiredArgsConstructor
    public enum OperatingSystem
    {
        WINDOWS(path -> new String[]{"rundll32", "url.dll,FileProtocolHandler", path}),
        MAC_OS(path -> new String[]{"open", path}),
        LINUX(path -> new String[]{"xdg-open", path}),
        SOLARIS(null),
        OTHER(null);

        private final Function<String, String[]> openPath;

        public void openFolder(File file)
        {
            if (openPath != null)
            {
                try
                {
                    execute(openPath.apply(file.getAbsolutePath()));
                }
                catch (IOException ignored)
                {
                }
            }
        }
    }

    /**
     * <h2>OperatingSystemVersion</h2>
     * <p>Wrapper class for an operating system version.</p>
     */
    @RequiredArgsConstructor
    @Getter
    public static class OperatingSystemVersion
    {
        private static final OperatingSystemVersion unknown = new OperatingSystemVersion(0, 0, 0, "", "");
        //--
        private final int major;
        private final int minor;
        private final int patch;
        private final String additional;
        private final String build;

        public static OperatingSystemVersion fromString(String value)
        {
            try
            {
                final SemanticVersion version = SemanticVersion.parse(value);

                return new OperatingSystemVersion(version.getVersionComponent(0),
                        version.getVersionComponentCount() >= 1 ? version.getVersionComponent(1) : 0,
                        version.getVersionComponentCount() >= 2 ? version.getVersionComponent(2) : 0,
                        version.getPrereleaseKey().orElse(""),
                        version.getBuildKey().orElse(""));
            }
            catch (VersionParsingException ex)
            {
                return unknown;
            }
        }
    }
}
