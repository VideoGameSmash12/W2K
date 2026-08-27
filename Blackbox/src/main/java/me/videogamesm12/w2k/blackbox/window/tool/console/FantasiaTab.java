package me.videogamesm12.w2k.blackbox.window.tool.console;

import com.google.gson.JsonElement;
import lombok.Getter;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.components.fantasia.Fantasia;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionPreProcessCommandEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionStartedEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.event.SessionStartedPreSetupEvent;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.CommandSender;
import me.videogamesm12.w2k.supervisor.components.fantasia.session.ISession;

import javax.swing.*;
import java.awt.*;

public class FantasiaTab extends AbstractTab<JTextArea>
{
    private final BlackboxSession session;

    public FantasiaTab()
    {
        super(new JTextArea());

        outputBox.setEditable(false);
        outputBox.setColumns(20);
        outputBox.setRows(5);
        outputBox.setLineWrap(true);
        outputBox.setWrapStyleWord(true);
        outputBox.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        session = new BlackboxSession(this);
        //--
        Fantasia.getInstance().getServer().addSession(session);
    }

    @Override
    public boolean shouldDisplay(JsonElement message)
    {
        return false;
    }

    @Override
    public String name()
    {
        return "Fantasia";
    }

    @Override
    public void showMessage(String text)
    {
        outputBox.append(text + "\n");

        if (outputBox.getSelectedText() == null)
        {
            outputBox.setCaretPosition(outputBox.getDocument().getLength());
        }
    }

    @Override
    public void clear()
    {
        outputBox.setText(null);
    }

    @Override
    public void send(String command)
    {
        final CommandSender sender = session.getSender();
        final SessionPreProcessCommandEvent event = new SessionPreProcessCommandEvent(sender.getSession(), command);
        Supervisor.getEventBus().post(event);

        if (!event.isCancelled())
        {
            Fantasia.getInstance().getServer().execute(command, sender);
        }
    }

    /**
     * <h2>BlackboxSession</h2>
     * <p>An implementation of ISession for connections from the Blackbox.</p>
     */
    @Getter
    public static class BlackboxSession implements ISession
    {
        private final FantasiaTab tab;
        private final CommandSender sender;

        public BlackboxSession(FantasiaTab tab)
        {
            Supervisor.getEventBus().post(new SessionStartedPreSetupEvent(this));
            this.tab = tab;
            this.sender = new CommandSender(this);
            Supervisor.getEventBus().post(new SessionStartedEvent(this));
        }

        @Override
        public String getConnectionIdentifier()
        {
            return "Blackbox";
        }

        @Override
        public boolean isConnected()
        {
            return Blackbox.getInstance().getMainWindow() != null;
        }

        @Override
        public void disconnect(boolean quiet)
        {
            // Silently do nothing because you can't disconnect like this
        }

        @Override
        public void sendMessage(String message)
        {
            tab.showMessage(message);
        }
    }
}
