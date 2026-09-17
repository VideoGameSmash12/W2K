package me.videogamesm12.w2k.kernel.event.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.wcom.Stage;

@Getter
@RequiredArgsConstructor
public class StateChangeEvent extends CustomEvent
{
    private final Stage previousStage;
    private final Stage newStage;
}
