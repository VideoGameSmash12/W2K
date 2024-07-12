package me.videogamesm12.w2k.kernel.event.lifecycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
@RequiredArgsConstructor
public class ClientStartedEvent extends CustomEvent
{
    private final Object client;
}
