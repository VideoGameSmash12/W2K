package me.videogamesm12.w2k.drivers.v1_21.required;

import com.google.gson.JsonElement;
import lombok.Getter;
import me.videogamesm12.w2k.drivers.v1_21.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v1_21.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_21.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.EntityEntry;
import me.videogamesm12.w2k.kernel.data.InventoryEntry;
import me.videogamesm12.w2k.kernel.data.MapEntry;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@WDriverMetadata(identifier = "121_version_bridge")
public class W121VersionBridgeDriver implements WVersionBridgeDriver
{
    @Getter
    private static final RegistryWrapper.WrapperLookup wrapperLookup = BuiltinRegistries.createWrapperLookup();

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
                Text.Serialization.fromJsonTree(ComponentUtils.serializeComponent(text), wrapperLookup));
    }

    @Override
    public String textToString(JsonElement text)
    {
        final Text parsed = Text.Serialization.fromJsonTree(text, wrapperLookup);
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
    public List<PlayerEntry> getOnlinePlayers()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().map(entry ->
                new PlayerEntry(entry.getProfile(), ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getDisplayName() != null ? entry.getDisplayName() : Text.literal(entry.getProfile().getName()), wrapperLookup)),
                        entry.getLatency(), entry.getGameMode() != null ? entry.getGameMode().getName() : "",
                        entry.getSkinTextures().model().getName(),
                        entry.getSkinTextures().texture().toString())).collect(Collectors.toList());
    }

    @Override
    public List<EntityEntry> getNearbyEntities()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return StreamSupport.stream(MinecraftClient.getInstance().world.getEntities().spliterator(), false)
                .map(entity -> new EntityEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(
                        entity.getDisplayName() != null ? entity.getDisplayName() : entity.getName(), wrapperLookup)),
                        EntityType.getId(entity.getType()).toString(),
                        String.format("%s, %s, %s", entity.getX(), entity.getY(), entity.getZ()),
                        entity.getId(),
                        entity.getUuid()))
                .toList();
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
                    return new MapEntry(entry.getKey().asString(),
                            String.valueOf(map.scale),
                            map.dimension.getValue().toString(),
                            map.centerX, map.centerZ, map.locked, map.colors);
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

        // TODO: NBT used to be the backbone of Minecraft and its items. This changed in 1.20.5 so majorly that I
        //  actually don't know how to work this new data component system.

        entries.addAll(inventory.main.stream().filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry ->
                new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                        wrapperLookup)),
                        entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                        entry.getCount(),
                        entry.getDamage(),
                        String.valueOf(slot.get()),
                        entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).toString())).toList());

        entries.addAll(inventory.armor.stream().filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry -> new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                wrapperLookup)),
                entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                entry.getCount(),
                entry.getDamage(),
                String.valueOf(slot.getAndIncrement()),
                entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).toString())).toList());

        entries.addAll(inventory.offHand.stream().filter(entry -> {
            slot.getAndIncrement();
            return !entry.isEmpty();
        }).map(entry -> new InventoryEntry(ComponentUtils.stringToElement(Text.Serialization.toJsonString(entry.getName(),
                wrapperLookup)),
                entry.getItem() != null ? Registries.ITEM.getId(entry.getItem()).toString() : "minecraft:unknown",
                entry.getCount(),
                entry.getDamage(),
                String.valueOf(slot.getAndIncrement()),
                entry.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).toString())).toList());

        return entries;
    }
}
