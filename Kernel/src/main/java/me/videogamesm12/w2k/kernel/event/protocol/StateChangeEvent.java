package me.videogamesm12.w2k.kernel.event.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.wcom.Stage;

/**
 * <h1>StateChangeEvent</h1>
 * <p>Event that is called when the {@link me.videogamesm12.w2k.kernel.communication.WCommunicationManager} transitions
 *  from one stage to another.</p>
 */
@Getter
@RequiredArgsConstructor
public class StateChangeEvent extends CustomEvent
{
    private final Stage previousStage;
    private final Stage newStage;
}
