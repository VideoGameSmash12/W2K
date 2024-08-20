package me.videogamesm12.w2k.kernel.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

import java.io.IOException;

public class SysUtils
{
    public static OperatingSystem getOperatingSystem()
    {
        final String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) return OperatingSystem.WINDOWS;
        else if (os.contains("mac")) return OperatingSystem.MAC_OS;
        else if (os.contains("linux") || os.contains("unix")) return OperatingSystem.LINUX;
        else if (os.contains("sunos")) return OperatingSystem.SOLARIS;
        else return OperatingSystem.OTHER;
    }

    public static OperatingSystemVersion getOperatingSystemVersion()
    {
        return OperatingSystemVersion.fromString(System.getProperty("os.version").toLowerCase());
    }

    public static Process execute(String... args) throws IOException, InterruptedException
    {
        return Runtime.getRuntime().exec(args);
    }

    public static boolean isUsingWayland()
    {
        return System.getenv("XDG_SESSION_TYPE").equalsIgnoreCase("wayland");
    }

    public enum OperatingSystem
    {
        WINDOWS,
        MAC_OS,
        LINUX,
        SOLARIS,
        OTHER
    }

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
