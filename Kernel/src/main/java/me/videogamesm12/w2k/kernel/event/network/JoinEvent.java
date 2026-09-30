package me.videogamesm12.w2k.kernel.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>JoinEvent</h1>
 * <p>Event that is called when the client joins a server.</p>
 */
@Getter
@RequiredArgsConstructor
public class JoinEvent extends CustomEvent
{
    private final Object handler;
    private final Object packetSender;
    private final Object client;
}
