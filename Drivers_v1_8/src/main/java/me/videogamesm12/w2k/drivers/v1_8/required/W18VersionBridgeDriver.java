package me.videogamesm12.w2k.drivers.v1_8.required;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.drivers.v1_8.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v1_8.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_8.mixin.accessor.EntityAccessor;
import me.videogamesm12.w2k.drivers.v1_8.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.EntityEntry;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@WDriverMetadata(identifier = "18_version_bridge")
public class W18VersionBridgeDriver implements WVersionBridgeDriver
{
    @Override
    public void disconnect()
    {
        if (MinecraftClient.getInstance().getNetworkHandler().getClientConnection() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        MinecraftClient.getInstance().getNetworkHandler().onDisconnected(new LiteralText("Disconnected by Supervisor"));
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
        if (MinecraftClient.getInstance().getNetworkHandler().getClientConnection() == null)
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
        if (MinecraftClient.getInstance().getNetworkHandler().getClientConnection() == null)
        {
            throw new IllegalStateException("Not connected to a server");
        }

        MinecraftClient.getInstance().player.sendChatMessage(message);
    }

    @Override
    public void displayMessage(Component text)
    {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
                Text.Serializer.deserialize(ComponentUtils.serializeComponentAsLegacy(text).toString()));
    }

    @Override
    public void removeEntitiesWithExceptions(String... exclusions)
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return;
        }

        ((ClientWorldAccessor) MinecraftClient.getInstance().world).getEntities().stream().filter(entity ->
                Arrays.stream(exclusions).noneMatch(type -> type.equalsIgnoreCase(((EntityAccessor) entity).getSavedEntityId() != null ?
                        ((EntityAccessor) entity).getSavedEntityId() :
                        entity instanceof PlayerEntity ? "minecraft:player" : "minecraft:unknown"))).forEach(entity ->
                MinecraftClient.getInstance().world.removeEntity(entity.getEntityId()));
    }

    @Override
    public String textToString(JsonElement text)
    {
        final Text parsed = Text.Serializer.deserialize(text.toString());
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
    public List<PlayerEntry> getOnlinePlayers()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().map(entry ->
                new PlayerEntry(entry.getProfile(), ComponentUtils.stringToElement(Text.Serializer.serialize(entry.getDisplayName())),
                        entry.getLatency(), entry.getGameMode().getName(), entry.getModel(),
                        entry.getSkinTexture().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityEntry> getNearbyEntities()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((ClientWorldAccessor) MinecraftClient.getInstance().world).getEntities().stream()
                .map(entity -> new EntityEntry(ComponentUtils.stringToElement(Text.Serializer.serialize(
                        entity.getCustomName() != null && !entity.getCustomName().isEmpty() ? new LiteralText(entity.getCustomName()) : new TranslatableText(entity.getTranslationKey()))),
                        ((EntityAccessor) entity).getSavedEntityId() != null ?
                                ((EntityAccessor) entity).getSavedEntityId() :
                                entity instanceof PlayerEntity ? "minecraft:player" : "minecraft:unknown",
                        String.format("%s, %s, %s", entity.x, entity.y, entity.z),
                        entity.getEntityId(),
                        entity.getUuid()))
                .collect(Collectors.toList());
    }
}
