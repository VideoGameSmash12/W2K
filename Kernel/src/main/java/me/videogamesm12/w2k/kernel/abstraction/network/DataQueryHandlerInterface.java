package me.videogamesm12.w2k.kernel.abstraction.network;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.concurrent.CompletableFuture;

public interface DataQueryHandlerInterface extends ObjectInterface
{
    CompletableFuture<CompoundBinaryTag> w2k$queryEntity(final int id);

    CompletableFuture<CompoundBinaryTag> w2k$queryBlock(final BlockPosInterface blockPos);

    void w2k$cancelAll();
}