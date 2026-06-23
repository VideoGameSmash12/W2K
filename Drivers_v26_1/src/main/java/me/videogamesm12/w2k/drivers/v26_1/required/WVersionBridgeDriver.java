package me.videogamesm12.w2k.drivers.v26_1.required;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import lombok.Getter;
import me.videogamesm12.w2k.drivers.v26_1.extra.IDebugScreen;
import me.videogamesm12.w2k.drivers.v26_1.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v26_1.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v26_1.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.drivers.v26_1.mixin.accessor.WorldAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntrySystemSpecs;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@WDriverMetadata(identifier = "1212_version_bridge")
public class WVersionBridgeDriver implements me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver
{
    private ChunkPos lastPos = null;

    @Override
    public void disconnect()
    {
        if (Minecraft.getInstance().getConnection() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        Minecraft.getInstance().getConnection().getConnection().disconnect(net.minecraft.network.chat.Component.literal("Disconnected by Supervisor"));
    }

    @Override
    public String getClientDebugInformation()
    {
        try
        {
            /*final Minecraft client = Minecraft.getInstance();
            final List<String> output = new ArrayList<>();
            List<Identifier> enabledEntries = ((IDebugScreen) client.debugEntries).w2k$getAllEntries();
            if (!enabledEntries.isEmpty())
            {
                final Level clientWorld = client.level;
                final Entity clientCameraEntity = client.getCameraEntity();

                final ChunkPos chunkPos = clientCameraEntity != null && clientWorld != null ?
                        ChunkPos.containing(clientCameraEntity.blockPosition()) :
                        null;

                if (!Objects.equals(lastPos, chunkPos))
                {
                    this.lastPos = chunkPos;
                }

                final LevelChunk clientChunk = clientWorld != null && lastPos != null ?
                        clientWorld.getChunk(lastPos.x(), lastPos.z())
                        : null;
                final LevelChunk serverChunk = client.getSingleplayerServer() != null && clientWorld != null ?
                        (LevelChunk) client.getSingleplayerServer().getLevel(clientWorld.dimension())
                                .getChunkSource().getChunkFuture(lastPos.x(), lastPos.z(), ChunkStatus.FULL, false)
                                .thenApply(result -> result.orElse(null))
                                .getNow(null) :
                        null;

                final DebugScreenDisplayer displayer = new DebugScreenDisplayer()
                {
                    @Override
                    public void addPriorityLine(String line)
                    {
                        output.addFirst(line);
                    }

                    @Override
                    public void addLine(String line)
                    {
                        output.add(line);
                    }

                    @Override
                    public void addToGroup(Identifier group, Collection<String> lines)
                    {
                        // Do nothing, we don't give a shit about groups right now
                    }

                    @Override
                    public void addToGroup(Identifier group, String lines)
                    {
                        // Do nothing, we don't give a shit about groups right now
                    }
                };

                enabledEntries.stream().map(DebugScreenEntries::getEntry).filter(Objects::nonNull).forEach(entry ->
                {
                    try
                    {
                        entry.display(displayer, clientWorld, clientChunk, serverChunk);
                    }
                    catch (IllegalStateException ignored)
                    {
                        // Damnit, Mojang!
                    }
                });
            }
            else
            {
                return "No debug menu entries are currently enabled.";
            }*/

            //return String.join("\n", output);
            return "1.21.11 changed how the debug F3 overlay works. I have yet to reimplement it.";
            //return String.join("\n", ((DHAccessor) ((IGHAccessor) Minecraft.getInstance().gui).getDebugOverlay()).getLeftText());
            //return String.join("\n", ((IDebugScreen) ((IGHAccessor) Minecraft.getInstance().gui).getDebugOverlay()).w2k$getRightText());
        }
        catch (Exception ignored)
        {
            ignored.printStackTrace();

            final String version = W2K.getInstance().getDriverManager().getVersionFetcher().getGameVersion();

            return String.join("\n", String.format("Minecraft %s (%s/%s)", version, version, ClientBrandRetriever.getClientModName()),
                String.valueOf(Minecraft.getInstance().getFps()));
        }
    }

    @Override
    public void runCommand(String command)
    {
        if (Minecraft.getInstance().getConnection() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        Minecraft.getInstance().getConnection().sendCommand(command);
    }

    @Override
    public void scheduleSafeShutdown()
    {
        Minecraft.getInstance().stop();
    }

    @Override
    public void sendMessage(String message)
    {
        if (Minecraft.getInstance().getConnection() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        Minecraft.getInstance().getConnection().sendChat(message);
    }

    @Override
    public void displayMessage(Component text)
    {
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(
                ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, ComponentUtils.serializeComponent(text)).getOrThrow());
    }

    @Override
    public void closeCurrentScreen()
    {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public String textToString(JsonElement text)
    {
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, text).result()
                .map(net.minecraft.network.chat.Component::getString).orElse("");
    }

    @Override
    public String getCurrentUsername()
    {
        return Minecraft.getInstance().getUser().getName();
    }

    @Override
    public List<IPlayerEntry> getPlayerList()
    {
        if (Minecraft.getInstance().getConnection() == null)
        {
            return Collections.emptyList();
        }

        return Minecraft.getInstance().getConnection().getListedOnlinePlayers().stream()
                .map(entry -> (IPlayerEntry) entry).toList();
    }

    @Override
    public List<EntityEntry> getNearbyEntities(boolean includeNbt)
    {
        if (Minecraft.getInstance().level == null)
        {
            return Collections.emptyList();
        }

        return StreamSupport.stream(Minecraft.getInstance().level.entitiesForRendering().spliterator(), false)
                .map(entity ->
                        new EntityEntry(
                                ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, entity.getDisplayName()).getOrThrow(),
                                net.minecraft.world.entity.EntityType.getKey(entity.getType()).toString(),
                                String.format("%s, %s, %s", entity.getX(), entity.getY(), entity.getZ()),
                                entity.getId(),
                                entity.getUUID(),
                                "yeah not implemented yet sorry"))
                                //includeNbt ? entity.save(TagValueOutput.createWithContext(ProblemReporter.DISCARDING)) : null))
                .toList();
    }

    @Override
    public List<InventoryEntry> getOpenInventory()
    {
        /*if (Minecraft.getInstance().level == null || Minecraft.getInstance().screen == null
                || Minecraft.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final AbstractContainerMenu handler = Minecraft.getInstance().player.containerMenu;

        return handler.slots.stream().map(slot ->
        {
            ItemStack entry = slot.getItem();

            final NbtComponent nbt = entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            return new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                    wrapperLookup)),
                    entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                    entry.getCount(),
                    entry.getDamage(),
                    String.valueOf(slot.id),
                    nbt.isEmpty() ? null : nbt.toString());
        }).filter(Objects::nonNull).toList();*/

        return Collections.emptyList();
    }

    @Override
    public List<MapEntry> getLoadedMaps()
    {
        /*if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((ClientWorldAccessor) MinecraftClient.getInstance().world).getMapStates().entrySet().stream()
                .map(entry ->
                {
                    final MapState map = entry.getValue();
                    return new MapEntry(entry.getKey().asString(),
                            String.valueOf(map.scale),
                            map.dimension.getValue().toString(),
                            map.centerX, map.centerZ, map.locked, map.colors,
                            map.writeNbt(new NbtCompound(), getWrapperLookup()).toString());
                }).collect(Collectors.toList());*/
        return Collections.emptyList();
    }


    @Override
    public List<InventoryEntry> getInventory()
    {
        /*if (MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        final List<InventoryEntry> entries = new ArrayList<>();
        final AtomicInteger slot = new AtomicInteger(0);

        // TODO: NBT used to be the backbone of Minecraft and its items. This changed in 1.20.5 so majorly that I
        //  actually don't know how to work this new data component system.

        entries.addAll(inventory.main.stream().filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry -> {
            final NbtComponent nbt = entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            return new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                    wrapperLookup)),
                    entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                    entry.getCount(),
                    entry.getDamage(),
                    String.valueOf(slot.get()),
                    nbt.isEmpty() ? null : nbt.toString());
        }).toList());

        entries.addAll(inventory.armor.stream().filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry ->
        {
            final NbtComponent nbt = entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            return new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                    wrapperLookup)),
                    entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                    entry.getCount(),
                    entry.getDamage(),
                    String.valueOf(slot.getAndIncrement()),
                    nbt.isEmpty() ? null : nbt.toString());
        }).toList());

        entries.addAll(inventory.offHand.stream().filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry ->
        {
            final NbtComponent nbt = entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            return new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                    wrapperLookup)),
                    entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                    entry.getCount(),
                    entry.getDamage(),
                    String.valueOf(slot.getAndIncrement()),
                    nbt.isEmpty() ? null : nbt.toString());
        }).toList());

        return entries;*/

        return Collections.emptyList();
    }

    @Override
    public List<TileEntry> getNearbyTileEntities()
    {
        /*if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((WorldAccessor) MinecraftClient.getInstance().world).getBlockEntityTickers().stream().filter(ticker -> !ticker.isRemoved())
                .filter(ticker -> ticker.getPos() != null)
                .filter(ticker -> MinecraftClient.getInstance().world.getBlockEntity(ticker.getPos()) != null).map(ticker -> {
                    final BlockEntity tileEntity = MinecraftClient.getInstance().world.getBlockEntity(ticker.getPos());
                    NbtCompound nbt = Objects.requireNonNull(tileEntity).toInitialChunkDataNbt(wrapperLookup);
                    return new TileEntry(Objects.requireNonNull(Registries.BLOCK_ENTITY_TYPE.getId(Objects.requireNonNull(tileEntity).getType())).toString(),
                            tileEntity.getPos().getX(),
                            tileEntity.getPos().getY(),
                            tileEntity.getPos().getZ(),
                            nbt.isEmpty() ? null : nbt.toString());
                }).toList();*/

        return Collections.emptyList();
    }
}
