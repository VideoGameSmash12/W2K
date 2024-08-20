/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.w2k.supervisor;

import me.videogamesm12.w2k.kernel.util.SysUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Entrypoints implements PreLaunchEntrypoint, ClientModInitializer
{
    private final Logger logger = LogManager.getLogger("Supervisor");

    @Override
    public void onPreLaunch()
    {
        Supervisor.setup();

        System.setProperty("java.awt.headless", "false");

        // Mitigates an issue in which doing certain tasks with the Blackbox on Linux in X11 causes X errors
        if (SysUtils.getOperatingSystem() == SysUtils.OperatingSystem.LINUX && !SysUtils.isUsingWayland() &&
                System.getProperty("me.videogamesm12.w2k.use_x11_pipeline_for_blackbox", "false").contains("f"))
        {
            logger.info("Greetings from W2K's developer - We are switching over the pipeline used by Java2D from the "
                    + "X11 pipeline to the OpenGL pipeline to work around stability issues in which the Blackbox "
                    + "may cause the X server to throw an error and kill the JVM.");
            logger.info("This may cause a performance drop overall when interacting with the Blackbox in certain ways "
                    + "like trying to resize the window. If you find this unacceptable or are experiencing issues you "
                    + "think are caused by this, you can either set the JVM command line property "
                    + "\"me.videogamesm12.w2k.dont_use_x11_workaround\" to \"true\" (which puts your client at risk of "
                    + "stability issues) or switch from using X11 to Wayland (and by extension, XWayland) instead.");
            System.setProperty("sun.java2d.opengl", "true");
        }
    }

    @Override
    public void onInitializeClient()
    {
        Supervisor.getInstance().getFlags().setGameStartedYet(true);
        Supervisor.getInstance().postStartup();
    }
}