package me.videogamesm12.w2k.kernel.event.miscellaneous;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import net.kyori.adventure.text.Component;

@Getter
@RequiredArgsConstructor
public class ChatMessageAddedEvent extends CustomEvent
{
    private final Component message;
}
