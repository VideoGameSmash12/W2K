package me.videogamesm12.w2k.toolbox.commands;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.command.EntitySelectorInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.command.ExecutionPath;
import me.videogamesm12.w2k.kernel.command.Parameters;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Parameters(name = "query", usage = "")
public class QueryCmd extends WCommand
{
    private final ForkJoinPool pool = new ForkJoinPool(4);

    @ExecutionPath({"entities", "<selector|w2k:wrapped/entities>"})
    public void queryEntities(final EntitySelectorInterface selectors)
    {
        final PlayNetworkHandlerInterface handler = W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .orElseThrow(() -> new IllegalStateException("Not connected to a server"));

        msg(Component.text("Initiating queries"));
        pool.submit(() ->
        {
            final List<CompoundBinaryTag> data = new ArrayList<>();

            selectors.w2k$getClientEntities().parallelStream()
                    .map(entity -> handler.w2k$getDataQueryHandler().w2k$queryEntity(entity.w2k$id()))
                    .peek(CompletableFuture::join)
                    .forEach(future -> future.whenComplete((result, exception) ->
                    {
                        if (exception != null)
                        {
                            W2K.getLogger().error("Error", exception);
                            return;
                        }

                        data.add(result);
                    }));

            msg(Component.text("Done, we got " + data.size() + " results"));
            data.forEach(tag ->
            {
                try
                {
                    W2K.getLogger().info(W2K.getInstance().getVersionAbstractionLayer().nbt().adventureToString(tag));
                }
                catch (IOException e)
                {
                }
            });
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
