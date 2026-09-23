package me.videogamesm12.w2k.kernel.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
@RequiredArgsConstructor
public class JoinEvent extends CustomEvent
{
    private final Object handler;
    private final Object packetSender;
    private final Object client;
}
