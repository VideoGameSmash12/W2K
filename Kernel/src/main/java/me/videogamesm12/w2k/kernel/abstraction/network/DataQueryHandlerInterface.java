package me.videogamesm12.w2k.kernel.abstraction.network;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.concurrent.CompletableFuture;

/**
 * <h1>DataQueryHandlerInterface</h1>
 * <p>A wrapper interface for {@code DataQueryHandler} or {@code DebugQueryHandler} (depending on your mappings)
 *  instances implemented using Mixins.
 * @implSpec        The methods this interface implements require you to implement some necessary upgrades to the data
 *                  query system. These upgrades should replace or append the system to use {@link CompletableFuture}s
 *                  that are temporarily stored in a {@link java.util.Map} or something similar based on a numerical ID.
 *                  Queries for entities should use the entity's ID as the transaction ID, as this allows you to tie
 *                  responses to specific entities and queries. Queries for block entities should use the hash code of
 *                  the coordinates as the transaction ID for the same reason.
 */
public interface DataQueryHandlerInterface extends ObjectInterface
{
    /**
     * Sends a query to a server for an entity based on the numerical ID and returns a {@link CompletableFuture} which
     *  will either time out or get completed as soon as the client receives the relevant response.
     * @param id        {@code int}
     * @return          {@link CompletableFuture<CompoundBinaryTag>}
     */
    CompletableFuture<CompoundBinaryTag> w2k$queryEntity(final int id);

    /**
     * Sends a query to the server for a block entity based on the {@link BlockPosInterface BlockPos} and returns a
     *  {@link CompletableFuture} which will either time out or get completed as soon as the client receives the
     *  relevant response.
     * @param blockPos  {@link BlockPosInterface}
     * @return          {@link CompletableFuture<CompoundBinaryTag>}
     */
    CompletableFuture<CompoundBinaryTag> w2k$queryBlock(final BlockPosInterface blockPos);

    /**
     * Cancels all active queries.
     */
    void w2k$cancelAll();
}