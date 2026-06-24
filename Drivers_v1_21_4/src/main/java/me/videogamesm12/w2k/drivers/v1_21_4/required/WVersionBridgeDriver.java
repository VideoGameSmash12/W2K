package me.videogamesm12.w2k.drivers.v1_21_4.required;

import com.google.gson.JsonElement;
import lombok.Getter;
import me.videogamesm12.w2k.drivers.v1_21_4.mixin.accessor.ClientWorldAccessor;
import me.videogamesm12.w2k.drivers.v1_21_4.mixin.accessor.DHAccessor;
import me.videogamesm12.w2k.drivers.v1_21_4.mixin.accessor.IGHAccessor;
import me.videogamesm12.w2k.drivers.v1_21_4.mixin.accessor.WorldAccessor;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.kernel.driver.base.WDriverMetadata;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

@WDriverMetadata(identifier = "1212_version_bridge")
public class WVersionBridgeDriver implements me.videogamesm12.w2k.kernel.driver.base.WVersionBridgeDriver
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
    public void closeCurrentScreen()
    {
        MinecraftClient.getInstance().setScreen(null);
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
    public List<IPlayerEntry> getPlayerList()
    {
        if (MinecraftClient.getInstance().getNetworkHandler() == null)
        {
            return Collections.emptyList();
        }

        return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream()
                .map(entry -> (IPlayerEntry) entry).toList();
    }

    @Override
    public List<IEntityEntry> getEntities()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return StreamSupport.stream(MinecraftClient.getInstance().world.getEntities().spliterator(), false)
                .map(IEntityEntry.class::cast)
                .toList();
    }

    @Override
    public List<IItemStackEntry> getOpenInventory()
    {
        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().currentScreen == null
                || MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final ScreenHandler handler = MinecraftClient.getInstance().player.currentScreenHandler;

        return handler.slots.stream().filter(Objects::nonNull).filter(Slot::hasStack).map(slot ->
                IItemStackEntry.class.cast(slot.getStack()).w2k$location(String.valueOf(slot.id))).filter(Objects::nonNull).toList();
    }

    @Override
    public List<IMapEntry> getMaps()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((ClientWorldAccessor) MinecraftClient.getInstance().world).getMapStates().entrySet().stream().map(entry ->
                ((IMapEntry) entry.getValue()).w2k$id(entry.getKey().asString())).toList();
    }

    @Override
    public List<IItemStackEntry> getPlayerInventory()
    {
        if (MinecraftClient.getInstance().player == null)
        {
            return Collections.emptyList();
        }

        final PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        final List<IItemStackEntry> entries = new ArrayList<>();
        final AtomicInteger slot = new AtomicInteger(0);

        entries.addAll(inventory.main.stream().filter(lol -> {
            slot.getAndIncrement();
            return !lol.isEmpty();
        }).map(entry -> IItemStackEntry.class.cast(entry).w2k$location(String.valueOf(slot))).toList());

        entries.addAll(inventory.armor.stream().filter(lol -> {
            slot.getAndIncrement();
            return !lol.isEmpty();
        }).map(entry -> IItemStackEntry.class.cast(entry).w2k$location(String.valueOf(slot))).toList());

        entries.addAll(inventory.offHand.stream().filter(lol -> {
            slot.getAndIncrement();
            return !lol.isEmpty();
        }).map(entry -> IItemStackEntry.class.cast(entry).w2k$location(String.valueOf(slot))).toList());

        return entries;
    }

    @Override
    public List<IBlockEntityEntry> getBlockEntities()
    {
        if (MinecraftClient.getInstance().world == null)
        {
            return Collections.emptyList();
        }

        return ((WorldAccessor) MinecraftClient.getInstance().world).getBlockEntityTickers().stream()
                .filter(ticker -> !ticker.isRemoved())
                .filter(ticker -> ticker.getPos() != null)
                .filter(ticker -> MinecraftClient.getInstance().world.getBlockEntity(ticker.getPos()) != null)
                .map(ticker -> ((IBlockEntityEntry) MinecraftClient.getInstance().world.getBlockEntity(ticker.getPos())))
                .toList();
    }
}
