package me.videogamesm12.w2k.integrator.integrations.replaymod.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
@RequiredArgsConstructor
public class StateUpdateEvent extends CustomEvent
{
    private final boolean stopped;

    private final boolean paused;

    private final boolean ableToStart;
}
