package me.videogamesm12.w2k.blackbox;

import me.videogamesm12.w2k.blackbox.command.BlackboxCmd;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.driver.base.Driver;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.util.SysUtils;

import java.util.Collections;
import java.util.List;

public class Bootstrapper extends Driver
{
    private List<WCommand> command;

    @Override
    public void init()
    {
        if (System.getProperties().getProperty("me.videogamesm12.w2k.no_blackbox", "f").toLowerCase().startsWith("t"))
        {
            W2K.getLogger().info("Not initializing Blackbox as by user request.");
            return;
        }

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
        this.command = Collections.singletonList(new BlackboxCmd());
    }

    @Override
    public List<WModule> modules()
    {
        return Collections.emptyList();
    }

    @Override
    public List<WCommand> commands()
    {
        return command;
    }
}