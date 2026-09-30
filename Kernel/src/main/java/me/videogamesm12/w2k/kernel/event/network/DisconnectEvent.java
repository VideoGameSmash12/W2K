package me.videogamesm12.w2k.kernel.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>DisconnectEvent</h1>
 * <p>Event that is called when the client disconnects from a server.</p>
 */
@Getter
@RequiredArgsConstructor
public class DisconnectEvent extends CustomEvent
{
    private final Object connection;
    private final Object client;
}
