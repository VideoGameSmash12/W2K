package me.videogamesm12.w2k.kernel.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import net.kyori.adventure.nbt.CompoundBinaryTag;

/**
 * <h1>DataQueryResponseEvent</h1>
 * <p>Event that is called when the client receives a data query response from the server.</p>
 */
@Getter
@RequiredArgsConstructor
public class DataQueryResponseEvent extends CustomEvent
{
    private final String id;
    private final BlockPosInterface position;
    private final CompoundBinaryTag nbt;
    private final String type;
    private final String source;
}
