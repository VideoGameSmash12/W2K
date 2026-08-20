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

package me.videogamesm12.w2k.supervisor.components.fantasia.listener;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.fantasia.Server;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.TelnetSession;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TelnetConnectionListener extends IConnectionListener
{
    @Getter
    private final ServerSocket socket;

    public TelnetConnectionListener(Server server) throws IOException
    {
        super("Fantasia-TelnetConnectionListener", server);

        // Set up Telnet server by finding an available port
        int port = Supervisor.getConfig().getFantasiaSettings().getPort();
        boolean looped = false;

        while (!portIsAvailable(port))
        {
            if (port >= 65535)
            {
                port = 0;
                looped = true;
            }
            else if (port == Supervisor.getConfig().getFantasiaSettings().getPort() && looped)
            {
                throw new IllegalStateException("Unable to find a free port despite our best efforts");
            }
            else
            {
                port++;
            }
        }

        if (port != Supervisor.getConfig().getFantasiaSettings().getPort())
        {
            W2K.getLogger().info("We weren't able to reserve the port defined in the configuration. So, we've instead reserved port {}", port);
        }

        this.socket = new ServerSocket(port, 999, Supervisor.getConfig().getFantasiaSettings().isNonLocalConnectionsAllowed() ? null : InetAddress.getLoopbackAddress());
    }

    @Override
    public void run()
    {
        if (Supervisor.getConfig().getFantasiaSettings().isNonLocalConnectionsAllowed())
        {
            W2K.getLogger().warn("*** DANGEROUS CONFIGURATION DETECTED ***");
            W2K.getLogger().warn("You are currently running Fantasia with non-local connections enabled.");
            W2K.getLogger().warn("While this does allow you to control it from other devices, it also means "
                    + "that anybody on your network (even a LogMeIn Hamachi network) can connect and do whatever they "
                    + "want with your client including crashing your client, running commands as you, and even sending "
                    + "chat messages as you.");
            W2K.getLogger().warn("Unless you are doing something that requires it, you are strongly urged "
                    + "to disable non-local connections from being able to connect.");
            W2K.getLogger().warn("You have been warned.");
        }

        while (!socket.isClosed())
        {
            Socket clientSocket;

            try
            {
                clientSocket = socket.accept();
            }
            catch (Exception ignored)
            {
                continue;
            }

            TelnetSession session = new TelnetSession(getServer(), clientSocket);
            getServer().addSession(session);
            session.start();
        }
    }

    @Override
    public void shutdown()
    {
        try
        {
            getSocket().close();
        }
        catch (Exception ignored)
        {
        }
    }
}