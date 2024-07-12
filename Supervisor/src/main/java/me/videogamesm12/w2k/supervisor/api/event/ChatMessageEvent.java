package me.videogamesm12.w2k.supervisor.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import net.minecraft.text.Text;

@Getter
@RequiredArgsConstructor
public class ChatMessageEvent extends CustomEvent
{
    final Text message;
}
