package me.videogamesm12.w2k.supervisor.components.fantasia.unix;

import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.fantasia.Fantasia;
import me.videogamesm12.w2k.supervisor.components.fantasia.Server;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionPreProcessCommandEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionStartedEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionStartedPreSetupEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.CommandSender;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.ISession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class UnixDomainSession extends Thread implements ISession
{
    private final Server server;
    private final SocketChannel socket;
    private final CommandSender sender;
    private final String identifier;

    public UnixDomainSession(Server server, SocketChannel socket)
    {
        this.server = server;
        this.socket = socket;
        this.sender = new CommandSender(this);
        this.identifier = String.valueOf(System.currentTimeMillis());
    }

    @Override
    public void run()
    {
        Supervisor.getEventBus().post(new SessionStartedPreSetupEvent(this));
        Supervisor.getEventBus().post(new SessionStartedEvent(this));

        while (isConnected())
        {
            String command;
            try
            {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int bytesRead = socket.read(buffer);

                if (bytesRead < 0)
                {
                    command = "";
                }
                else
                {
                    byte[] bytes = new byte[bytesRead];
                    buffer.flip();
                    buffer.get(bytes);
                    command = new String(bytes).replaceAll("\n$", "");
                }
            }
            catch (Exception ex)
            {
                break;
            }

            if (command.isEmpty())
            {
                continue;
            }

            try
            {
                final SessionPreProcessCommandEvent event = new SessionPreProcessCommandEvent(sender.getSession(), command);
                Supervisor.getEventBus().post(event);

                if (!event.isCancelled())
                {
                    Fantasia.getInstance().getServer().execute(command, sender);
                }
            }
            catch (Throwable ex)
            {
                Fantasia.getServerLogger().error("An error occurred whilst attempting to execute command " + command, ex);
                sendMessage("Command error: " + ex.getMessage());
            }
        }
    }

    /**
     * Returns the timestamp of when the session was established, which is used as an identifier.
     * @return  String
     */
    @Override
    public String getConnectionIdentifier()
    {
        return identifier;
    }

    @Override
    public boolean isConnected()
    {
        return socket.isConnected();
    }

    @Override
    public void disconnect(boolean quiet)
    {
        if (!quiet)
        {
            sendMessage("Disconnecting...");
        }

        try
        {
            socket.close();
            server.removeSession(this);
        }
        catch (Exception ignored)
        {
        }
    }

    @Override
    public void sendMessage(String message)
    {
        // Hack that adds a new line after the message
        message = message + "\r\n";
        //--
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();

        while (buffer.hasRemaining())
        {
            try
            {
                socket.write(buffer);
            }
            catch (IOException ex)
            {
                // DEBUG
                ex.printStackTrace();
                return;
            }
        }
    }
}
