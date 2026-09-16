package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.BulkEvent;
import me.videogamesm12.w2k.kernel.event.network.DataQueryResponseEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.FileSetting;
import me.videogamesm12.w2k.toolbox.util.DumpUtil;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class QueryLogger extends WModule
{
    public final FileSetting location = register(new FileSetting("location", "Location", DumpUtil.getDumpsFolder()));
    public final BooleanSetting alert = register(new BooleanSetting("alert", "Show Alert", true));

    public QueryLogger()
    {
        super("Query Logger",
                "Dumps 'F3 + I' query responses received from the server to their own dedicated dump files.");
    }

    @Subscribe
    public void onDataQueryResult(final DataQueryResponseEvent event)
    {
        if (isEnabled())
        {
            logQueryResult(event, location.get()).whenComplete(((path, throwable) ->
            {
                if (throwable != null)
                {
                    W2K.getLogger().error("Failed to dump queried {}", event.getType(), throwable);
                    return;
                }

                W2K.getLogger().info("Successfully dumped {} query response to {}", event.getType(), path);

                if (alert.get())
                {
                    W2K.getInstance().getVersionAbstractionLayer().getLocalPlayer()
                            .ifPresent(player ->
                                    player.w2k$displayMessage(Component.text("Query response logged to " + path + ".", NamedTextColor.GREEN)));
                }
            }));
        }
    }

    @Subscribe
    public void onBulkDataQueryResult(final BulkEvent<DataQueryResponseEvent> bulkEvent)
    {
        if (isEnabled() && bulkEvent.applicable(DataQueryResponseEvent.class))
        {
            final long timestamp = System.currentTimeMillis();
            final File folder = new File(location.get(), "bulk_query-" + timestamp);
            folder.mkdirs();

            CompletableFuture.runAsync(() ->
            {
                final AtomicInteger success = new AtomicInteger();

                bulkEvent.getEvents().stream()
                        .map(event -> logQueryResult(event, folder)
                                .whenComplete((file, throwable) ->
                                {
                                    if (throwable != null)
                                    {
                                        W2K.getLogger().error("Failed to log query result", throwable);
                                        return;
                                    }

                                    success.incrementAndGet();
                                    W2K.getLogger().info("Successfully wrote dump to {}", file.getName());
                                }))
                        .forEach(CompletableFuture::join);

                if (alert.get())
                {
                    W2K.getInstance().getVersionAbstractionLayer().getLocalPlayer()
                            .ifPresent(player -> player.w2k$displayMessage(Component.translatable("w2k.toolbox.query.bulk.success", Component.text(success.get())
                                    .color(NamedTextColor.GREEN))));
                }
            });
        }
    }

    private CompletableFuture<File> logQueryResult(final DataQueryResponseEvent event, final File destinationFolder)
    {
        final CompletableFuture<File> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() ->
        {
            final File destinationFile = new File(destinationFolder,
                    (event.getType() + "_" + event.getId()).replace(":", "-")
                    + "-"
                    + (destinationFolder == location.get() ? System.currentTimeMillis() : event.hashCode())
                    + ".nbt");

            try (FileOutputStream stream = new FileOutputStream(destinationFile))
            {
                BinaryTagIO.writer().write(event.getNbt(), stream, BinaryTagIO.Compression.GZIP);
                future.complete(destinationFile);
            }
            catch (IOException ex)
            {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }
}
