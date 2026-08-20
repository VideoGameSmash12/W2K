/*
 * Copyright (c) 2026 Video
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

package me.videogamesm12.w2k.supervisor.components.fantasia.listener;

import lombok.Getter;
import me.videogamesm12.w2k.supervisor.components.fantasia.Server;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class IConnectionListener extends Thread
{
    @Getter
    private final Server server;

    public IConnectionListener(final String identifier, final Server server)
    {
        super(identifier);
        this.server = server;
    }

    public final void start()
    {
        super.start();
    }

    public abstract void shutdown();

    /**
     * Utility method to check if a network port is available for use
     * @param port  Integer
     * @return      True if the port specified is not yet occupied
     */
    public final boolean portIsAvailable(int port)
    {
        port = Math.min(Math.max(port, 0), 65535);

        ServerSocket socket;
        boolean success;

        try
        {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            success = true;
        }
        catch (IOException ex)
        {
            socket = null;
            success = false;
        }

        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException ignored)
            {
            }
        }

        return success;
    }
}