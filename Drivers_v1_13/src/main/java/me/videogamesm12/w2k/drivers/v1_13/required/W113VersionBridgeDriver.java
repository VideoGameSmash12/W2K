package me.videogamesm12.w2k.drivers.v1_13.required;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.videogamesm12.w2k.drivers.v1_13.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_13.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WDriverMetadata(identifier = "12_version_bridge", maxVersion = "1.12.2", minVersion = "1.12.2", minProtocolVersion = 0, maxProtocolVersion = 0)
public class W113VersionBridgeDriver implements WVersionBridgeDriver
{
    private static final Gson GSON = new Gson();

    @Override
    public void disconnect()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
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
}
