package me.videogamesm12.w2k.kernel.driver.base;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.EntityEntry;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface WVersionBridgeDriver extends WDriver
{
    void disconnect();

    String getClientDebugInformation();

    void runCommand(String command);

    void scheduleSafeShutdown();

    void sendMessage(String message);

    void displayMessage(Component text);

    String textToString(JsonElement text);

    List<PlayerEntry> getOnlinePlayers();

    List<EntityEntry> getNearbyEntities();
}
