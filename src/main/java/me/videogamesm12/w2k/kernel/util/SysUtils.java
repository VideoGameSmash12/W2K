package me.videogamesm12.w2k.kernel.util;

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

    public enum OperatingSystem
    {
        WINDOWS,
        MAC_OS,
        LINUX,
        SOLARIS,
        OTHER
    }
}
