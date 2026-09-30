package me.videogamesm12.w2k.kernel.abstraction.network;

import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * <h1>PlayNetworkHandlerInterface</h1>
 * <p>A wrapper interface for {@code ClientPlayNetworkHandler} or {@code ClientPacketListener} (depending on your
 *  mappings) instances which are implemented using Mixins.</p>
 * <p>Instances of this interface can be obtained by calling {@link BaseVersionAbstractionLayer#networkHandler()}.</p>
 */
public interface PlayNetworkHandlerInterface extends ObjectInterface
{
    /**
     * Send a chat message as the user in this connection.
     * @param message   String
     */
    void w2k$sendChatMessage(final String message);

    /**
     * Executes a command as the user in this connection.
     * @param message   String
     */
    void w2k$sendCommand(final String message);

    /**
     * Terminates the connection from the server with the given reason.
     * @param reason    {@link Component}
     */
    void w2k$disconnect(final Component reason);

    /**
     * Gets a list of all online players from the perspective of this network handler.
     * @return                  {@code List<PlayerListEntry>}
     * @param <PlayerListEntry> {@link PlayerListEntryInterface}
     */
    <PlayerListEntry extends PlayerListEntryInterface> List<PlayerListEntry> w2k$getOnlinePlayers();

    /**
     * Gets the {@code DataQueryHandler} for this connection.
     * @return  {@link DataQueryHandlerInterface}
     */
    DataQueryHandlerInterface w2k$getDataQueryHandler();
}
