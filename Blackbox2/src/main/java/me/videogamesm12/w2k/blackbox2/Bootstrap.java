package me.videogamesm12.w2k.blackbox2;

import lombok.Getter;
import me.videogamesm12.w2k.blackbox2.gui.ConnectingWindow;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.driver.base.WDriver;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.util.SysUtils;

@WDriverMetadata(identifier = "blackbox")
public class Bootstrap implements WDriver
{
    @Getter
    private Blackbox blackbox;

    @Override
    public void onInitialize()
    {
        // Perform certain workarounds/behavioral changes depending on the operating system
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
            case SOLARIS:
            case OTHER:
            default:
            {
                W2K.getLogger().warn("The Blackbox has not been properly tested under this operating system, so in the "
                        + "interest of maintaining client stability, it has been disabled.");
                return;
            }
        }

        //new ConnectingWindow().setVisible(true);
        blackbox = new Blackbox();
        blackbox.setup();
    }
}
