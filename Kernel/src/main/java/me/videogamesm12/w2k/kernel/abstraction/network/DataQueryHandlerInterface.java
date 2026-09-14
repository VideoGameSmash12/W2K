package me.videogamesm12.w2k.kernel.abstraction.network;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.concurrent.CompletableFuture;

public interface DataQueryHandlerInterface extends ObjectInterface
{
    void w2k$setExpectedTransactionId(int value);

    int w2k$getExpectedTransactionId();

    void w2k$blockQueries();

    void w2k$unblockQueries();

    boolean w2k$areQueriesBlocked();

    CompletableFuture<CompoundBinaryTag> w2k$queryEntity(int id);

    void w2k$cancelAll();
}