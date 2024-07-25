package me.videogamesm12.w2k.drivers.v1_19_1.required;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.drivers.v1_19_1.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v1_19_1.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_19_1.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.EntityEntry;
import me.videogamesm12.w2k.kernel.data.MapEntry;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@WDriverMetadata(identifier = "191_version_bridge")
public class W119VersionBridgeDriver implements WVersionBridgeDriver
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

        MinecraftClient.getInstance().player.sendCommand(command);
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

        MinecraftClient.getInstance().player.sendChatMessage(message, null);
    }

    @Override
    public void displayMessage(Component text)
    {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                Text.Serializer.fromJson(ComponentUtils.serializeComponent(text)));
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
    public List<PlayerEntry> getOnlinePlayers()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().map(entry ->
                new PlayerEntry(entry.getProfile(), Text.Serializer.toJsonTree(entry.getDisplayName()), entry.getLatency(),
                        entry.getGameMode() != null ? entry.getGameMode().getName() : "", entry.getModel(),
                        entry.getSkinTexture().toString())).collect(Collectors.toList());
    }

    @Override
    public List<EntityEntry> getNearbyEntities()
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
                    return new MapEntry(entry.getKey(), String.valueOf(map.scale), map.dimension.toString(),
                            map.centerX, map.centerZ, map.locked, map.colors);
                }).collect(Collectors.toList());
    }
}
