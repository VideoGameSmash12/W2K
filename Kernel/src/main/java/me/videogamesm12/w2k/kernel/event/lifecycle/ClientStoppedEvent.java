package me.videogamesm12.w2k.kernel.event.lifecycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>ClientStoppedEvent</h1>
 * <p>Event that is called when the Minecraft client stops. This event is not called when the client crashes, so you
 * should also listen for {@link ClientCrashedEvent} as well in your event listeners.</p>
 * @implNote    In versions where the Fabric API is available, it's better to use that instead of hooking into Minecraft
 *              yourself to avoid causing mod conflicts. The Fabric API is so commonly used that it's unusual to see an
 *              instance without it.
 */
@Getter
@RequiredArgsConstructor
public class ClientStoppedEvent extends CustomEvent
{
    private final Object client;
}
