package me.videogamesm12.w2k.kernel.event.hud;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import net.kyori.adventure.text.Component;

/**
 * <h1>ChatMessageAddedEvent</h1>
 * <p>Event that is called when the client adds a chat message to the in-game HUD.</p>
 */
@Getter
@RequiredArgsConstructor
public class ChatMessageAddedEvent extends CustomEvent
{
    private final Component message;
}
