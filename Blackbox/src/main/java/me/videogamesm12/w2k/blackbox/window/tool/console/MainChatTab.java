package me.videogamesm12.w2k.blackbox.window.tool.console;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.hud.ChatMessageAddedEvent;
import net.kyori.adventure.text.Component;

import javax.swing.*;
import java.awt.*;

public class MainChatTab extends AbstractTab<JList<String>>
{
    private final DefaultListModel<String> model;

    public MainChatTab()
    {
        super(new JList<>(new DefaultListModel<>()));
        this.model = (DefaultListModel<String>) outputBox.getModel();
        outputBox.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        W2K.getEventBus().register(this);
    }

    @Override
    public void showMessage(String text)
    {
        model.addElement(text);
    }

    @Override
    public void clear()
    {
        model.clear();
    }

    @Override
    public String name()
    {
        return "Chat";
    }

    @Subscribe
    public void onChatMessage(ChatMessageAddedEvent event)
    {
        String message;
        try
        {
            message = W2K.getInstance().getVersionAbstractionLayer().text().adventureToString(event.getMessage(), true);
        }
        catch (Exception ex)
        {
            message = W2K.getInstance().getVersionAbstractionLayer().text().adventureToString(event.getMessage(), false);
        }

        showMessage(message);
    }
}
