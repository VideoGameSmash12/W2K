package me.videogamesm12.w2k.kernel.driver.base;

import com.google.common.annotations.Beta;
import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.*;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>WVersionBridgeDriver</h1>
 * <p>A specific kind of driver which is used by W2K's components to call Minecraft code (which may differ between
 * versions) without relying on version specific code.</p>
 * <p>In the future, this system may be revised to be better organized so that everything isn't in one big driver.</p>
 */
public interface WVersionBridgeDriver extends WDriver
{
    /**
     * Disconnects the client from whatever server it is connected to.
     */
    void disconnect();

    /**
     * Retrieves information normally seen in the game's debug overlay when you press F3.
     * @return  String
     */
    String getClientDebugInformation();

    /**
     * Executes a command from in-game.
     * @param command   String
     */
    void runCommand(String command);

    /**
     * Schedules a safe client shutdown.
     */
    void scheduleSafeShutdown();

    /**
     * Sends a chat message from in-game.
     * @param message   String
     */
    void sendMessage(String message);

    /**
     * Display a {@link Component message component} in-game.
     * @param text  Component
     */
    void displayMessage(Component text);

    /**
     * Close whatever screen is currently open.
     */
    @Beta
    void closeCurrentScreen();

    /**
     * Converts a {@link JsonElement} text component to plain text.
     * @param text  JsonElement
     * @return      String
     */
    String textToString(JsonElement text);

    /**
     * Gets the player's current username.
     * @return  String
     */
    String getCurrentUsername();

    /**
     * Generates a {@link IPlayerEntry} list consisting of all online players.
     * @return A list of wrapped player entries
     */
    default List<IPlayerEntry> getPlayerList()
    {
        return new ArrayList<>();
    }

    /**
     * Generates an {@link IEntityEntry} list consisting of all entities currently in memory.
     * @return              A list of wrapped entities
     */
    List<IEntityEntry> getEntities();

    /**
     * Generates an {@link IItemStackEntry} list consisting of every item in the currently open screen (if present).
     * @return A list of wrapped item stacks
     */
    List<IItemStackEntry> getOpenInventory();

    /**
     * Generates a {@link IMapEntry} list consisting of all map data currently in memory.
     * @return A list of wrapped maps
     */
    default List<IMapEntry> getMaps()
    {
        return Collections.emptyList();
    }

    /**
     * Generates an {@link IItemStackEntry} list consisting of every item in the player's inventory.
     * @return A list of wrapped item stacks
     */
    default List<IItemStackEntry> getPlayerInventory()
    {
        return new ArrayList<>();
    }

    /**
     * Generates an {@link IBlockEntityEntry} list consisting of every block entity in memory.
     * @return A list of wrapped block entities
     */
    List<IBlockEntityEntry> getBlockEntities();
}
