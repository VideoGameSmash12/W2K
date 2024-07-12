package me.videogamesm12.w2k.blackbox;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.util.SysUtils;
import net.fabricmc.api.ClientModInitializer;

public class Entrypoints implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        switch (SysUtils.getOperatingSystem())
        {
            case MAC_OS:
            case LINUX:
            {
                // https://bugs.openjdk.org/browse/JDK-8056151
                System.setProperty("sun.java2d.xrender", "f");
            }
            case WINDOWS:
            {
                break;
            }
            case SOLARIS:
            case OTHER:
            default:
            {
                W2K.getLogger().warn("The Blackbox has not been properly tested under this operating system, so in the "
                        + "interest of maintaining client stability, it has been disabled.");
                return;
            }
        }

        Blackbox.setup();
    }
}