package me.videogamesm12.w2k.kernel.event.network;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@RequiredArgsConstructor
public class DisconnectEvent extends CustomEvent
{
    private final Object connection;
    private final Object client;
}
