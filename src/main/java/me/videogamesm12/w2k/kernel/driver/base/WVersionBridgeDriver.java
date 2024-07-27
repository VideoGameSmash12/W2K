package me.videogamesm12.w2k.kernel.driver.base;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.*;
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

    void closeCurrentScreen();

    String textToString(JsonElement text);

    String getCurrentUsername();

    List<PlayerEntry> getOnlinePlayers();

    List<EntityEntry> getNearbyEntities();

    List<MapEntry> getLoadedMaps();

    List<InventoryEntry> getInventory();

    List<TileEntry> getNearbyTileEntities();
}
