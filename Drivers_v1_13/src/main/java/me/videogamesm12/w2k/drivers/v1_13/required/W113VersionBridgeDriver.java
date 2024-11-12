package me.videogamesm12.w2k.drivers.v1_13.required;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.drivers.v1_13.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_13.mixin.accessor.EnigmaClass4070Accessor;
import me.videogamesm12.w2k.drivers.v1_13.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.drivers.v1_13.mixin.accessor.PersistentStateManagerAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@WDriverMetadata(identifier = "13_version_bridge")
public class W113VersionBridgeDriver implements WVersionBridgeDriver
{
    @Override
    public void disconnect()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().disconnect(new LiteralText("Disconnected by Supervisor"));
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

        MinecraftClient.getInstance().player.sendChatMessage(command);
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

        MinecraftClient.getInstance().player.sendChatMessage(message);
    }

    @Override
    public void displayMessage(Component text)
    {
        System.out.println(ComponentUtils.serializeComponentAsLegacy(text).toString());

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                Text.Serializer.deserializeText(ComponentUtils.serializeComponentAsLegacy(text).toString()));
    }

    @Override
    public void closeCurrentScreen()
    {
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public String textToString(JsonElement text)
    {
        final Text parsed = Text.Serializer.deserializeText(text.toString());
        if (parsed == null)
        {
            return "";
        }
        else
        {
            return parsed.asFormattedString();
        }
    }

    @Override
    public String getCurrentUsername()
    {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    @Override
    public List<PlayerEntry> getOnlinePlayers()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().map(entry ->
                new PlayerEntry(entry.getProfile(), ComponentUtils.stringToElement(Text.Serializer.serialize(entry.getDisplayName())),
                        entry.getLatency(), entry.getGameMode().getGameModeName(), entry.getModel(),
                        entry.getSkinTexture().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityEntry> getNearbyEntities(boolean includeNbt)
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().world.entities.stream()
                .map(entity -> new EntityEntry(ComponentUtils.stringToElement(Text.Serializer.serialize(
                        new LiteralText(entity.method_15541() != null ? entity.method_15541().getString() : entity.getEntityName()))),
                        EntityType.getId(entity.method_15557()) != null ? EntityType.getId(entity.method_15557()).toString() : entity.getClass().getName(),
                        String.format("%s, %s, %s", entity.x, entity.y, entity.z),
                        entity.getEntityId(),
                        entity.getUuid(),
                        entity.toNbt(new NbtCompound()).toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryEntry> getOpenInventory()
    {
        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().currentScreen == null
                || MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final ScreenHandler handler = MinecraftClient.getInstance().player.openScreenHandler;

        return handler.slots.stream().filter(Objects::nonNull).map(slot ->
        {
            ItemStack entry = slot.getStack();
            if (entry == null)
            {
                return null;
            }

            return new InventoryEntry(Text.Serializer.method_20183(entry.getName()),
                    entry.getItem() != null && Registry.ITEM.getId(entry.getItem()) != null ?
                            Registry.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                    entry.getCount(),
                    entry.getDamage(),
                    String.valueOf(slot.id),
                    entry.getNbt() != null ? entry.getNbt().toString() : null);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<MapEntry> getLoadedMaps()
    {
        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().world.method_16399() == null)
        {
            return Collections.emptyList();
        }

        final List<MapEntry> mapStates = new ArrayList<>();

        ((EnigmaClass4070Accessor) MinecraftClient.getInstance().world.method_16399()).getPersistentStateManagers()
                .values().stream().map(manager -> ((PersistentStateManagerAccessor) manager).getStateMap().entrySet()
                        .stream().filter(entry -> entry.getKey().startsWith("map_")).map(entry ->
                        {
                            final MapState state = (MapState) entry.getValue();
                            final Identifier world = Registry.DIMENSION_TYPE.getId(state.field_19747);
                            return new MapEntry(state.method_17914(), String.valueOf(state.scale), world != null ?
                                    world.toString() : "minecraft:unknown", state.xCenter, state.zCenter, false,
                                    state.colors, state.toNbt(new NbtCompound()).toString());
                        }).collect(Collectors.toList())).forEach(mapStates::addAll);

        return mapStates;
    }

    @Override
    public List<InventoryEntry> getInventory()
    {
        if (MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final PlayerInventory inventory = MinecraftClient.getInstance().player.inventory;
        final List<InventoryEntry> entries = new ArrayList<>();
        final AtomicInteger slot = new AtomicInteger(0);

        entries.addAll(inventory.field_15082.stream().map(entry ->
                new InventoryEntry(Text.Serializer.method_20183(entry.getName()),
                        entry.getItem() != null && Registry.ITEM.getId(entry.getItem()) != null ?
                                Registry.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).collect(Collectors.toList()));

        entries.addAll(inventory.field_15083.stream().map(entry ->
                new InventoryEntry(Text.Serializer.method_20183(entry.getName()),
                        entry.getItem() != null && Registry.ITEM.getId(entry.getItem()) != null ?
                                Registry.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).collect(Collectors.toList()));

        entries.addAll(inventory.field_15084.stream().map(entry ->
                new InventoryEntry(Text.Serializer.method_20183(entry.getName()),
                        entry.getItem() != null && Registry.ITEM.getId(entry.getItem()) != null ?
                                Registry.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).collect(Collectors.toList()));

        return entries;
    }

    @Override
    public List<TileEntry> getNearbyTileEntities()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().world.tickingBlockEntities.stream().map(tile ->
        {
            final NbtCompound nbt = new NbtCompound();
            tile.toNbt(nbt);

            return new TileEntry(Objects.requireNonNull(Registry.BLOCK.getId(tile.method_16783().getBlock())).toString(),
                    tile.getPos().getX(),
                    tile.getPos().getY(),
                    tile.getPos().getZ(),
                    nbt.toString());
        }).collect(Collectors.toList());
    }
}
