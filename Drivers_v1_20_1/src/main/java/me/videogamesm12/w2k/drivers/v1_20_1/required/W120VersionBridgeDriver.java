package me.videogamesm12.w2k.drivers.v1_20_1.required;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.drivers.v1_20_1.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v1_20_1.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_20_1.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.drivers.v1_20_1.mixin.accessor.WorldAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedPlayerListEntry;
import net.kyori.adventure.text.Component;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@WDriverMetadata(identifier = "120_version_bridge")
public class W120VersionBridgeDriver implements WVersionBridgeDriver
{
    @Override
    public void disconnect()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        MinecraftClient.getInstance().getNetworkHandler().getConnection().disconnect(Text.literal("Disconnected by Supervisor"));
    }

    @Override
    public String getClientDebugInformation()
    {
        try
        {
            return String.join("\n", ((DHAccessor) ((IGHAccessor) MinecraftClient.getInstance().inGameHud).getDebugHud()).getLeftText());
        }
        catch (Exception ignored)
        {
            final String version = W2K.getInstance().getDriverManager().getVersionFetcher().getGameVersion();

            return String.join("\n", String.format("Minecraft %s (%s/%s)", version, version, ClientBrandRetriever.getClientModName()),
                MinecraftClient.getInstance().fpsDebugString);
        }
    }

    @Override
    public void runCommand(String command)
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        MinecraftClient.getInstance().getNetworkHandler().sendChatCommand(command);
    }

    @Override
    public void scheduleSafeShutdown()
    {
        MinecraftClient.getInstance().scheduleStop();
    }

    @Override
    public void sendMessage(String message)
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }

    @Override
    public void displayMessage(Component text)
    {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                Text.Serializer.fromJson(ComponentUtils.serializeComponent(text)));
    }

    @Override
    public void closeCurrentScreen()
    {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public String textToString(JsonElement text)
    {
        final Text parsed = Text.Serializer.fromJson(text);
        if (parsed == null)
        {
            return "";
        }
        else
        {
            return parsed.getString();
        }
    }

    @Override
    public String getCurrentUsername()
    {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    @Override
    public List<EntityEntry> getNearbyEntities(boolean includeNbt)
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return StreamSupport.stream(MinecraftClient.getInstance().world.getEntities().spliterator(), false)
                .map(entity -> new EntityEntry(Text.Serializer.toJsonTree(
                        entity.getDisplayName() != null ? entity.getDisplayName() : Text.literal(entity.getEntityName())),
                        EntityType.getId(entity.getType()).toString(),
                        String.format("%s, %s, %s", entity.getX(), entity.getY(), entity.getZ()),
                        entity.getId(),
                        entity.getUuid(),
                        includeNbt ? entity.writeNbt(new NbtCompound()).toString() : null))
                .toList();
    }

    @Override
    public List<InventoryEntry> getOpenInventory()
    {
        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().currentScreen == null
                || MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final ScreenHandler handler = MinecraftClient.getInstance().player.currentScreenHandler;

        return handler.slots.stream().filter(Objects::nonNull).map(slot ->
        {
            ItemStack entry = slot.getStack();
            if (entry == null)
            {
                return null;
            }

            return new InventoryEntry(Text.Serializer.toJsonTree(entry.getName()),
                    entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                    entry.getCount(),
                    entry.getDamage(),
                    String.valueOf(slot.id),
                    entry.getNbt() != null ? entry.getNbt().toString() : null);
        }).filter(Objects::nonNull).toList();
    }

    @Override
    public List<MapEntry> getLoadedMaps()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((ClientWorldAccessor) MinecraftClient.getInstance().world).getMapStates().entrySet().stream()
                .map(entry ->
                {
                    final MapState map = entry.getValue();
                    return new MapEntry(entry.getKey(), String.valueOf(map.scale), map.dimension.toString(),
                            map.centerX, map.centerZ, map.locked, map.colors, map.writeNbt(new NbtCompound()).toString());
                }).collect(Collectors.toList());
    }

    @Override
    public List<InventoryEntry> getInventory()
    {
        if (MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        final List<InventoryEntry> entries = new ArrayList<>();
        final AtomicInteger slot = new AtomicInteger(0);

        entries.addAll(inventory.main.stream().map(entry ->
                new InventoryEntry(Text.Serializer.toJsonTree(entry.getName()),
                        entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).toList());

        entries.addAll(inventory.armor.stream().map(entry ->
                new InventoryEntry(Text.Serializer.toJsonTree(entry.getName()),
                        entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).toList());

        entries.addAll(inventory.offHand.stream().map(entry ->
                new InventoryEntry(Text.Serializer.toJsonTree(entry.getName()),
                        entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).toList());

        return entries;
    }

    @Override
    public List<TileEntry> getNearbyTileEntities()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((WorldAccessor) MinecraftClient.getInstance().world).getBlockEntityTickers().stream().filter(ticker -> !ticker.isRemoved())
                .filter(ticker -> ticker.getPos() != null)
                .filter(ticker -> MinecraftClient.getInstance().world.getBlockEntity(ticker.getPos()) != null).map(ticker -> {
                    final BlockEntity tileEntity = MinecraftClient.getInstance().world.getBlockEntity(ticker.getPos());
                    return new TileEntry(Objects.requireNonNull(Registries.BLOCK_ENTITY_TYPE.getId(Objects.requireNonNull(tileEntity).getType())).toString(),
                            tileEntity.getPos().getX(),
                            tileEntity.getPos().getY(),
                            tileEntity.getPos().getZ(),
                            tileEntity.toInitialChunkDataNbt().toString());
                }).toList();
    }

    @Override
    public List<WrappedPlayerListEntry> getPlayers()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().map(entry ->
                (WrappedPlayerListEntry) entry).toList();
    }
}
