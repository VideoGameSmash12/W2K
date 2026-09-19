package me.videogamesm12.w2k.val.v1_21_11.miscellaneous;

import com.mojang.blaze3d.systems.RenderSystem;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import me.videogamesm12.w2k.val.v1_21_11.mixin.DebugHudAccessor;
import me.videogamesm12.w2k.val.v1_21_11.mixin.DebugHudProfileAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.client.gui.hud.debug.DebugHudProfile;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DebugHudHandler
{
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final List<String> leftLines = new ArrayList<>();

    public CompletableFuture<List<String>> getLeftLines()
    {
        final CompletableFuture<List<String>> results = new CompletableFuture<>();

        // Refer to our cache instead if we have a good cache. May not be fully accurate but it'll do
        if (!leftLines.isEmpty() && client.getDebugHud().shouldShowDebugHud())
        {
            results.complete(leftLines);
            return results;
        }

        // If we aren't displaying anything or got caught in a bad time, we'll just compute it lol
        CompletableFuture.runAsync(() ->
        {
            leftLines.clear();

            // This is all the identifiers that the user has elected to have enabled in some fashion
            final List<Identifier> identifiers = ((DebugHudProfileAccessor) client.debugHudEntryList).getVisibilityMap().entrySet().stream()
                    .filter(entry -> entry.getValue() != DebugHudEntryVisibility.NEVER)
                    .map(Map.Entry::getKey)
                    .toList();

            // If none of them are enabled at all, we will just display a very basic message
            if (identifiers.isEmpty())
            {
                leftLines.add("You don't have any visible entries in the Debug HUD. So instead, here's a simple FPS string!");
                leftLines.add("FPS: " + client.getCurrentFps());
                results.complete(leftLines);
                return;
            }

            final DebugHudAccessor accessor = (DebugHudAccessor) client.getDebugHud();

            ChunkPos chunkPos = null;
            if (client.getCameraEntity() != null && client.world != null)
            {
                chunkPos = new ChunkPos(client.getCameraEntity().getBlockPos());
            }

            if (!Objects.equals(accessor.getPos(), chunkPos))
            {
                accessor.setPos(chunkPos);
                accessor.resetChunk();
            }

            final List<String> all = new ArrayList<>();

            final List<String> general = new ArrayList<>();
            final List<String> priority = new ArrayList<>();
            final Map<Identifier, List<String>> map = new LinkedHashMap<>();
            final DebugHudLines debugHudLines = new DebugHudLines()
            {
                @Override
                public void addPriorityLine(String string)
                {
                    priority.add(string);
                }

                @Override
                public void addLine(String string)
                {
                    general.add(string);
                }

                @Override
                public void addLinesToSection(Identifier identifier, Collection<String> collection)
                {
                    map.computeIfAbsent(identifier, (id) -> new ArrayList<>()).addAll(collection);
                }

                @Override
                public void addLineToSection(Identifier identifier, String string)
                {
                    map.computeIfAbsent(identifier, (id) -> new ArrayList<>()).add(string);
                }
            };
            World world = accessor.getWorld();

            // As inefficient as this is, it seems to be the only way...
            W2K.getLogger().info("Debug - Preparing to call rendering tasks");
            identifiers.stream()
                    .map(DebugHudEntries::get)
                    .filter(Objects::nonNull)
                    .map(entry ->
                    {
                        final CompletableFuture<Void> computation = new CompletableFuture<>();

                        RenderSystem.queueFencedTask(() ->
                        {
                            W2K.getLogger().info("Debug - Running task for entry {}", entry);
                            entry.render(debugHudLines, world, accessor.getClientChunk(), accessor.getChunk());
                            computation.complete(null);
                        });

                        return computation;
                    })
                    .forEach(CompletableFuture::join);

            all.add("---- Priority ----");
            all.addAll(priority);
            all.add("");
            all.add("---- General ----");
            all.addAll(general);
            all.add("");
            map.forEach((id, strings) ->
            {
                all.add("---- " + id.toString() + "----");
                all.addAll(strings);
                all.add("");
            });

            W2K.getLogger().info("Debug - Done!");

            results.complete(all);

            //W2K.getInstance().getVersionAbstractionLayer().execute();

        });

        return results;
    }

    public List<String> getLeftLinesNow()
    {
        return leftLines;
    }

    public void cacheLeftLines(final List<String> leftLines)
    {
        this.leftLines.clear();
        this.leftLines.addAll(leftLines);
    }
}
