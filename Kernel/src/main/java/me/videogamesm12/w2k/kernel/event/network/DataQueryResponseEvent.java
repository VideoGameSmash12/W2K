package me.videogamesm12.w2k.kernel.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import me.videogamesm12.w2k.kernel.event.CustomEvent;
import net.kyori.adventure.nbt.CompoundBinaryTag;

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
