package me.videogamesm12.w2k.blackbox.window.tool.console;

import com.google.gson.JsonElement;

public class MainChatTab extends AbstractTab
{
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
