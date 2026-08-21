package me.videogamesm12.w2k.supervisor.components.fantasia.unix;

import me.videogamesm12.w2k.supervisor.components.fantasia.Fantasia;
import me.videogamesm12.w2k.supervisor.components.fantasia.Server;
import me.videogamesm12.w2k.supervisor.components.fantasia.listener.IConnectionListener;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnixDomainConnectionListener extends IConnectionListener
{
    private final Path socketFolder = Path.of("/tmp/");
    private final Path socketPath;
    //--
    private final ServerSocketChannel channel;

    public UnixDomainConnectionListener(Server server) throws IOException
    {
        super("Fantasia-UnixDomainConnectionListener", server);
        //--
        int id = 0;
        while (!socketIsAvailable(id))
        {
            id++;
        }
        this.socketPath = socketFolder.resolve(getSocketFileName(id));
        //--
        this.channel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
        this.channel.bind(UnixDomainSocketAddress.of(this.socketPath));
        //--
    }

    @Override
    public void run()
    {
        while (channel.isOpen())
        {
            SocketChannel clientChannel;

            try
            {
                clientChannel = channel.accept();
            }
            catch (Exception ex)
            {
                continue;
            }

            UnixDomainSession session = new UnixDomainSession(getServer(), clientChannel);
            getServer().addSession(session);
            session.start();
        }
    }

    @Override
    public void interrupt()
    {
        // Nuke the file
        try
        {
            Files.deleteIfExists(socketPath);
        }
        catch (Exception ex)
        {
            Fantasia.getServerLogger().error("Failed to delete socket file", ex);
        }

        super.interrupt();
    }

    @Override
    public void shutdown()
    {
        try
        {
            channel.close();
        }
        catch (Exception ignored)
        {
        }

        try
        {
            Files.deleteIfExists(socketPath);
        }
        catch (Exception ex)
        {
            Fantasia.getServerLogger().error("Failed to delete socket file", ex);
        }
    }

    public boolean socketIsAvailable(int number)
    {
        ServerSocketChannel channel;
        boolean success;
        final Path path = socketFolder.resolve(getSocketFileName(number));

        try
        {
            channel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
            channel.bind(UnixDomainSocketAddress.of(path));
            success = true;
        }
        catch (IOException ex)
        {
            channel = null;
            success = false;
        }

        if (channel != null)
        {
            try
            {
                channel.close();
                Files.delete(path);
            }
            catch (IOException ignored)
            {
            }
        }

        return success;
    }

    private String getSocketFileName(int number)
    {
        return String.format("w2k-fantasia-%d.socket", number);
    }
}
