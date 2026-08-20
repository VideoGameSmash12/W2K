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

package me.videogamesm12.w2k.supervisor.components.fantasia;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.event.ClientFreezeEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.command.*;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionPreProcessCommandEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionStartedEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionStartedPreSetupEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.listener.IConnectionListener;
import me.videogamesm12.w2k.supervisor.components.fantasia.listener.TelnetConnectionListener;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.CommandSender;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.ISession;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public class Server extends Thread
{
    private final List<ISession> sessions = new ArrayList<>();
    private final Map<String, FCommand> commands = new HashMap<>();
    //--
    private IConnectionListener connectionListener;

    public Server()
    {
        super("Fantasia-Server");
    }

    @Override
    public void run()
    {
        Fantasia.getServerLogger().info("Starting up listeners...");
        Supervisor.getEventBus().register(this);
        try
        {
            final String connectionTypeKey = Supervisor.getConfig().getFantasiaSettings().getConnectionType();

            // Even if we don't have a connection type defined, we still want to allow it to register commands, as
            //  Fantasia can also be accessed using the Blackbox, which bypasses the entire connection type system.
            if (connectionTypeKey != null)
            {
                connectionListener = ConnectionType.get(connectionTypeKey).createListener(this);
                connectionListener.start();
            }
        }
        catch (UnsupportedOperationException ex)
        {
            Fantasia.getServerLogger().error("Unix sockets are not supported on this operating system. Try using something else like Telnet. You'll still be able to access it through the Blackbox, but not through anything external.");
        }
        catch (Throwable ex)
        {
            Fantasia.getServerLogger().error("Failed to start the selected connection listener. You'll still be able to access it through the Blackbox, but not through anything external. Stacktrace:", ex);
        }

        Fantasia.getServerLogger().info("Registering commands...");
        registerCommand(CrashCmd.class);
        registerCommand(ChatCmd.class);
        registerCommand(DisconnectCmd.class);
        registerCommand(DumpCmd.class);
        registerCommand(ExitCmd.class);
        registerCommand(FPSCmd.class);
        registerCommand(HelpCmd.class);
        // TODO: Port this command with W2K's new system
        //registerCommand(ListCmd.class);
        registerCommand(RunCmd.class);
        registerCommand(ShutdownCmd.class);
        registerCommand(StacktraceDumpCmd.class);
        Fantasia.getServerLogger().info("Commands registered");
    }

    @Override
    public void interrupt()
    {
        shutdown();
        super.interrupt();
    }

    public void addSession(ISession session)
    {
        sessions.add(session);
    }

    public void removeSession(ISession session)
    {
        sessions.remove(session);
    }

    public void shutdown()
    {
        try
        {
            connectionListener.shutdown();
        }
        catch (Exception ignored)
        {
        }

        for (ISession session : sessions)
        {
            CompletableFuture.runAsync(() -> session.disconnect(false));
        }

        sessions.clear();
    }

    public void registerCommand(Class<? extends FCommand> cmd)
    {
        try
        {
            FCommand command = cmd.getDeclaredConstructor().newInstance();
            commands.put(command.getName(), command);
        }
        catch (Exception ex)
        {
            Fantasia.getServerLogger().warn("Failed to register command {}", cmd.getName(), ex);
        }
    }

    public void execute(String command, CommandSender sender)
    {
        final String[] brokenUp = command.split(" ");
        if (brokenUp.length == 0)
        {
            return;
        }

        final String commandLabel = brokenUp[0].toLowerCase();
        if (commands.containsKey(commandLabel))
        {
            FCommand cmd = commands.get(commandLabel);
            cmd.execute(sender, ArrayUtils.remove(brokenUp, 0));
        }
        else
        {
            sender.sendMessage("Unknown command. Type 'help' for a list of commands.");
        }
    }

    public void broadcast(String message)
    {
        sessions.forEach(session -> CompletableFuture.runAsync(() -> session.sendMessage(message)));
    }

    @Subscribe
    public void onClientFreeze(ClientFreezeEvent event)
    {
        broadcast(" ** WARNING: CLIENT FREEZE DETECTED, LAST RENDERED " + event.getLastRendered() + " MS AGO ** ");
    }

    @Subscribe
    public void onSessionStartedPreSetup(SessionStartedPreSetupEvent event)
    {
        W2K.getLogger().info("{} connected.", event.getSession().getConnectionIdentifier());
    }

    @Subscribe
    public void onSessionStarted(SessionStartedEvent event)
    {
        ISession session = event.getSession();
        session.sendMessage("  ___         _           _");
        session.sendMessage(" | __|_ _ _ _| |_ __ _ __(_)__ _");
        session.sendMessage(" | _/ _` | ' \\  _/ _` (_-< / _` |");
        session.sendMessage(" |_|\\__,_|_||_\\__\\__,_/__/_\\__,_|");
        session.sendMessage(" --============================--");
        session.sendMessage(" Welcome to Fantasia, the Supervisor's internal console.\n"
                + " This allows you control it even before the Blackbox &\n"
                + " main game have even initialized.");
        session.sendMessage(" --");
        session.sendMessage(" Use 'help' for a list of commands.");
    }

    @Subscribe
    public void onSessionPreProcessCommand(SessionPreProcessCommandEvent event)
    {
        W2K.getLogger().info("{} issued client command '{}'", event.getSession().getConnectionIdentifier(), event.getCommand());
    }

    private boolean portIsAvailable(int port)
    {
        if (port < 0 || port > 65535)
        {
            return false;
        }

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