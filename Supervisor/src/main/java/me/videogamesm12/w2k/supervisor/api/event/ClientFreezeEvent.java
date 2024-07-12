package me.videogamesm12.w2k.supervisor.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
@RequiredArgsConstructor
public class ClientFreezeEvent extends CustomEvent
{
    private final long lastRendered;
}