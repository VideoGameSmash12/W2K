package me.videogamesm12.w2k.supervisor.api.event;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@Getter
@RequiredArgsConstructor
public class ChatMessageEvent extends CustomEvent
{
    final JsonElement message;
}
