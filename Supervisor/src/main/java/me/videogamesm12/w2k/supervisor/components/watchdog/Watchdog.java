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

package me.videogamesm12.w2k.supervisor.components.watchdog;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.SVComponent;
import me.videogamesm12.w2k.supervisor.api.event.ClientFreezeEvent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Watchdog extends Thread implements SVComponent
{
    public static long LAST_RENDERED_TIME = 0L;
    //--
    private final ScheduledExecutorService freezeDetector = new ScheduledThreadPoolExecutor(1);

    @Override
    public String identifier()
    {
        return "w2k:watchdog";
    }

    @Override
    public void setup()
    {
        start();
    }

    @Override
    public void run()
    {
        W2K.getEventBus().register(this);

        freezeDetector.scheduleAtFixedRate(() ->
        {
            long time = System.currentTimeMillis();

            // Has the game even started up yet? Is freeze detection even enabled?
            if (!Supervisor.getInstance().getFlags().isGameStartedYet() ||
                    !Supervisor.getConfig().getWatchdogSettings().isFreezeDetectionEnabled()
                    || LAST_RENDERED_TIME == 0L)
            {
                return;
            }

            // The client hasn't rendered something in 5 seconds. This usually indicates that the game has frozen.
            if (time - LAST_RENDERED_TIME >= Supervisor.getConfig().getWatchdogSettings().getFreezeDetectionThreshold())
            {
                long lastRendered = time - LAST_RENDERED_TIME;
                W2K.getLogger().warn("The Supervisor has detected a client-side freeze. Last rendered {}ms ago", lastRendered);
                Supervisor.getEventBus().post(new ClientFreezeEvent(lastRendered));
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown()
    {
        freezeDetector.shutdownNow();
    }
}