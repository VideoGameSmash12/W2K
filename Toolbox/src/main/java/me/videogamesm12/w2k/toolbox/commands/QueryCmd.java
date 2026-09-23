package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.event.BulkEvent;
import me.videogamesm12.w2k.kernel.event.network.DataQueryResponseEvent;
import me.videogamesm12.w2k.toolbox.modules.QueryLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;

public class QueryCmd extends WCommand
{
    private final ForkJoinPool pool = new ForkJoinPool(4);

    public QueryCmd()
    {
        super("query");
    }

    @Override
    public boolean available()
    {
        return W2K.getInstance().getModuleManager().getModule(QueryLogger.class).isEnabled();
    }

    @ExecutionPath({"entities", "<selector|w2k:wrapped/entities>"})
    public void queryEntities(final EntitySelectorInterface selectors)
    {
        final PlayNetworkHandlerInterface handler = W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .orElseThrow(() -> new IllegalStateException("Not connected to a server"));

        msg(Component.translatable("w2k.toolbox.query.bulk.starting", NamedTextColor.GRAY));
        pool.submit(() ->
        {
            final List<DataQueryResponseEvent> events = new ArrayList<>();

            selectors.w2k$getClientEntities().parallelStream()
                    .map(entity -> handler.w2k$getDataQueryHandler().w2k$queryEntity(entity.w2k$id())
                            .whenComplete((result, throwable) ->
                            {
                                if (throwable != null)
                                {
                                    msg(Component.translatable("w2k.toolbox.query.failed.error", NamedTextColor.RED));
                                    W2K.getLogger().error("Failed to query entity {}", entity.w2k$id(), throwable);
                                    return;
                                }

                                events.add(new DataQueryResponseEvent(entity.w2k$type(), entity.w2k$blockPos(), result, "entity", "w2k-toolbox:query_command"));
                            }))
                    .forEach(CompletableFuture::join);

            W2K.getEventBus().post(new BulkEvent<>(events, DataQueryResponseEvent.class));
        });

    }

    @ExecutionPath({"entity", "<id|brigadier:integer>"})
    public void queryEntityById(int id)
    {
        final PlayNetworkHandlerInterface handler = W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .orElseThrow(() -> new IllegalStateException("Not connected to a server"));

        handler.w2k$getDataQueryHandler().w2k$queryEntity(id).whenComplete((result, exception) ->
        {
            if (exception != null)
            {
                if (exception instanceof TimeoutException)
                {
                    msg(Component.text("Timed out", NamedTextColor.RED));
                    return;
                }

                W2K.getLogger().error("Error whilst trying to query entity {}", id, exception);
                return;
            }

            try
            {
                msg(Component.text(W2K.getInstance().getVersionAbstractionLayer().nbt().adventureToString(result)));
            }
            catch (IOException ex)
            {
            }
        });
    }

    @ExecutionPath("entity")
    public void queryEntityById()
    {
        Optional<EntityInterface> target = W2K.getInstance().getVersionAbstractionLayer().getTargetedEntity();
        if (target.isPresent())
        {
            queryEntityById(target.get().w2k$id());
        }
        else
        {
            msg(Component.text("You are not looking at an entity", NamedTextColor.RED));
        }
    }
}
