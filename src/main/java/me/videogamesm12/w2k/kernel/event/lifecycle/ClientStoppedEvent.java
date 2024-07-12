package me.videogamesm12.w2k.kernel.event.lifecycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import net.minecraft.client.MinecraftClient;

@Getter
@RequiredArgsConstructor
public class ClientStoppedEvent extends CustomEvent
{
    private final Object client;
}
