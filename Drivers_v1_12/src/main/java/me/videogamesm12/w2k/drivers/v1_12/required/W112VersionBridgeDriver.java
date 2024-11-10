package me.videogamesm12.w2k.drivers.v1_12.required;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.drivers.v1_12.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v1_12.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_12.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.drivers.v1_12.mixin.accessor.PersistentStateManagerAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@WDriverMetadata(identifier = "12_version_bridge")
public class W112VersionBridgeDriver implements WVersionBridgeDriver
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
            return parsed.asUnformattedString();
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

        return ((ClientWorldAccessor) MinecraftClient.getInstance().world).getEntities().stream()
                .map(entity -> new EntityEntry(ComponentUtils.stringToElement(Text.Serializer.serialize(
                        entity.getCustomName() != null && !entity.getCustomName().isEmpty() ? new LiteralText(entity.getCustomName()) : new TranslatableText(entity.getTranslationKey()))),
                        EntityType.getId(entity) != null ? Objects.requireNonNull(EntityType.getId(entity)).toString() :
                                entity instanceof PlayerEntity ? "minecraft:player" : "minecraft:unknown",
                        String.format("%s, %s, %s", entity.x, entity.y, entity.z),
                        entity.getEntityId(),
                        entity.getUuid(),
                        entity.toNbt(new NbtCompound()).toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MapEntry> getLoadedMaps()
    {
        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().world.getPersistentStateManager() == null)
        {
            return Collections.emptyList();
        }

        return ((PersistentStateManagerAccessor) MinecraftClient.getInstance().world.getPersistentStateManager())
                .getStateMap().entrySet().stream().filter(entry -> entry.getKey().startsWith("map_")).map(entry -> {
                    final MapState state = ((MapState) entry.getValue());
                    return new MapEntry(state.id, String.valueOf(state.scale), String.valueOf(state.dimensionId),
                            state.xCenter, state.zCenter, false, state.colors);
                }).collect(Collectors.toList());
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
                new InventoryEntry(ComponentUtils.serializeComponentAsLegacy(Component.text(entry.getCustomName())),
                        entry.getItem() != null && Item.REGISTRY.getIdentifier(entry.getItem()) != null ?
                                Item.REGISTRY.getIdentifier(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).collect(Collectors.toList()));

        entries.addAll(inventory.field_15083.stream().map(entry ->
                new InventoryEntry(ComponentUtils.serializeComponentAsLegacy(Component.text(entry.getCustomName())),
                        entry.getItem() != null && Item.REGISTRY.getIdentifier(entry.getItem()) != null ?
                                Item.REGISTRY.getIdentifier(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.getAndIncrement()),
                        entry.getNbt() != null ? entry.getNbt().toString() : null)).collect(Collectors.toList()));

        entries.addAll(inventory.field_15084.stream().map(entry ->
                new InventoryEntry(ComponentUtils.serializeComponentAsLegacy(Component.text(entry.getCustomName())),
                        entry.getItem() != null && Item.REGISTRY.getIdentifier(entry.getItem()) != null ?
                                Item.REGISTRY.getIdentifier(entry.getItem()).toString() : "minecraft:unknown",
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

            return new TileEntry(Block.REGISTRY.getIdentifier(tile.getBlock()).toString(),
                    tile.getPos().getX(),
                    tile.getPos().getY(),
                    tile.getPos().getZ(),
                    nbt.toString());
        }).collect(Collectors.toList());
    }
}
