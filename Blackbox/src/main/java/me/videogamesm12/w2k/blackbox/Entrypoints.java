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
            case LINUX:
            {
                // https://bugs.openjdk.org/browse/JDK-8056151
                System.setProperty("sun.java2d.xrender", "f");
            }
            case WINDOWS:
            {
                break;
            }
            case MAC_OS:
            {
                W2K.getLogger().warn("Due to systemic issues in how the Blackbox operates (which will change... "
                        + "eventually) which cause the client to hang up on start-up, in the interest of maintaining "
                        + "client stability, it has been disabled.");
                W2K.getLogger().warn("For more information, please see https://github.com/VideoGameSmash12/W2K/issues/7");
                return;
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