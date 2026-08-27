package me.videogamesm12.w2k.kernel.event.protocol;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import me.videogamesm12.wcom.WPacket;

@Getter
@RequiredArgsConstructor
public class WPacketReceivedEvent<T extends WPacket> extends CustomEvent
{
    private final String id;
    private final T packet;
}
