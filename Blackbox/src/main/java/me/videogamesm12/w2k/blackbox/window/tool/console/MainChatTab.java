package me.videogamesm12.w2k.blackbox.window.tool.console;

import com.google.gson.JsonElement;

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
    public boolean shouldDisplay(JsonElement message)
    {
        return true;
    }

    @Override
    public String name()
    {
        return "Chat";
    }
}
