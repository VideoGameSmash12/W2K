package me.videogamesm12.w2k.kernel.event.hud;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;

/**
 * <h1>ChatMessageAddedEvent</h1>
 * <p>Event that is called when the client adds a chat message to the in-game HUD.</p>
 */
@Getter
@RequiredArgsConstructor
public class ChatMessageAddedEvent extends CustomEvent
{
    private final JsonElement message;

    private Component getMessageAsAdventure()
    {
        return ComponentUtils.deserializeComponent(message);
    }
}
