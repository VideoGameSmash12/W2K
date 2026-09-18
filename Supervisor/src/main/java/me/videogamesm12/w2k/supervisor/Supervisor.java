/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.w2k.supervisor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.*;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCleanedUpAfterCrashEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.event.network.packet.*;
import me.videogamesm12.w2k.kernel.event.render.BlockEntityRenderEvent;
import me.videogamesm12.w2k.kernel.event.render.EntityRenderEvent;
import me.videogamesm12.w2k.kernel.event.render.GameRenderEvent;
import me.videogamesm12.w2k.supervisor.api.SVComponent;
import me.videogamesm12.w2k.supervisor.components.fantasia.Fantasia;
import me.videogamesm12.w2k.supervisor.components.flags.Flags;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h1>Supervisor</h1>
 * A major component in W2K, offering better control over the client
 */
public class Supervisor extends Thread
{
    @Getter
    private static final EventBus eventBus = new EventBus();
    //--
    @Getter
    private static Supervisor instance;
    @Getter
    private static Configuration config;
    //--
    private final List<SVComponent> components = new ArrayList<>();
    @Getter
    private final Flags flags;

    public Supervisor()
    {
        super("Supervisor");
        this.flags = new Flags();
    }

    public static void setup()
    {
        instance = new Supervisor();
        instance.start();
    }

    @Override
    public void run()
    {
        W2K.getEventBus().register(this);
        W2K.getLogger().info("Setting up the Supervisor...");
        instance = this;
        //--
        W2K.getLogger().info("Loading configuration...");
        config = loadConfiguration();
        //--
        W2K.getLogger().info("Setting up components...");
        components.add(new Fantasia());
        components.add(new Watchdog());
        components.forEach(SVComponent::setup);
        W2K.getLogger().info("Supervisor components successfully set up.");
    }

    @Subscribe
    public void onClientStarted(ClientStartedEvent event)
    {
        flags.setGameStartedYet(true);
    }

    @Subscribe
    public void onClientStopped(ClientStoppedEvent event)
    {
        shutdown();
    }

    @Subscribe
    public void onClientCleanUpAfterCrash(ClientCleanedUpAfterCrashEvent event)
    {
        if (FabricLoader.getInstance().isModLoaded("notenoughcrashes"))
        {
            return;
        }

        shutdown();
    }

    @Subscribe
    public void onCrashReport(PopulateCrashReportEvent event)
    {
        final List<String> lines = new ArrayList<>();
        lines.add("Mitigations:");
        lines.add("\tRendering:");
        config.getRenderingSettings().getSettings().forEach((name, value) -> lines.add("\t\t" + name + ": " + value));
        lines.add("\tNetwork:");
        config.getNetworkSettings().getSettings().forEach((name, value) -> lines.add("\t\t" + name + ": " + value));

        components.forEach(component ->
        {
            lines.add(component.identifier() + ":");
            lines.addAll(component.crashReportDetails());
        });

        event.appendSection("Supervisor", lines.toArray(new String[0]));
    }

    @Subscribe
    public void onGameRender(GameRenderEvent event)
    {
        if (config.getRenderingSettings().isGameRenderingDisabled())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onBlockEntityRender(BlockEntityRenderEvent event)
    {
        if (config.getRenderingSettings().isTileEntityRenderingDisabled()
                || config.getRenderingSettings().isGameRenderingDisabled())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onEntityRender(EntityRenderEvent event)
    {
        if (config.getRenderingSettings().isTileEntityRenderingDisabled()
                || config.getRenderingSettings().isGameRenderingDisabled())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onIncomingEntityPacket(IncomingEntitySpawnPacketEvent event)
    {
        if (config.getNetworkSettings().isIgnoringEntitySpawns())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onExplosionPacket(IncomingExplosionPacketEvent event)
    {
        if (config.getNetworkSettings().isIgnoringExplosions())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onLightUpdatePacket(IncomingLightUpdatePacketEvent event)
    {
        if (config.getNetworkSettings().isIgnoringLightUpdates())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onParticleSpawnPacket(IncomingParticleSpawnPacketEvent event)
    {
        if (config.getNetworkSettings().isIgnoringParticleSpawns())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onMapUpdatePacket(IncomingMapUpdatePacketEvent event)
    {
        if (config.getNetworkSettings().isIgnoringMapUpdates())
        {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onOpenScreenPacket(IncomingOpenScreenPacketEvent event)
    {
        if (config.getNetworkSettings().isIgnoringScreens())
        {
            event.setCancelled(true);
        }
    }

    public Configuration loadConfiguration()
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "w2k-supervisor.json");

        if (file.exists())
        {
            try
            {
                return new Gson().fromJson(new FileReader(file), Configuration.class);
            }
            catch (Exception ex)
            {
                W2K.getLogger().error("Failed to read Supervisor configuration", ex);
                return new Configuration();
            }
        }
        else
        {
            return new Configuration();
        }
    }

    public void saveConfiguration()
    {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "w2k-supervisor.json");
        try (FileWriter writer = new FileWriter(file))
        {
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(config));
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to write Supervisor configuration", ex);
        }
    }

    public void postStartup()
    {
        //CommandSystem.registerCommand(FantasiaCommand.class);
    }

    public void chatMessage(String message)
    {
        W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .ifPresent(handler -> handler.w2k$sendChatMessage(message));
    }

    public void disconnect()
    {
        W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .ifPresent(handler -> handler.w2k$disconnect(Component.text("Disconnected by Supervisor")));
    }

    public void runCommand(String command)
    {
        W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .ifPresent(handler -> handler.w2k$sendCommand(command));
    }

    public List<PlayerListEntryInterface> getPlayerList()
    {
        return W2K.getInstance().getVersionAbstractionLayer().networkHandler()
                .map(PlayNetworkHandlerInterface::w2k$getOnlinePlayers)
                .orElse(Collections.emptyList());
    }

    public List<EntityInterface> getNearbyEntities()
    {
        return W2K.getInstance().getVersionAbstractionLayer().getLocalWorld()
                .map(ClientWorldInterface::w2k$getEntities)
                .orElse(Collections.emptyList());
    }

    public List<BlockEntityInterface> getNearbyBlockEntities()
    {
        return W2K.getInstance().getVersionAbstractionLayer().getLocalWorld()
                .map(ClientWorldInterface::w2k$getBlockEntities)
                .orElse(Collections.emptyList());
    }

    public List<MapStateInterface> getLoadedMaps()
    {
        return W2K.getInstance().getVersionAbstractionLayer().getLocalWorld()
                .map(ClientWorldInterface::w2k$getMapStates)
                .map(map -> map.entrySet().stream()
                        .map(entry -> entry.getValue().w2k$id(entry.getKey()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public List<ItemStackInterface> getInventory()
    {
        return W2K.getInstance().getVersionAbstractionLayer().getLocalPlayer()
                .map(ClientPlayerEntityInterface::w2k$getInventory)
                .orElse(Collections.emptyList());
    }

    public void closeCurrentScreen()
    {
        W2K.getInstance().getVersionAbstractionLayer().closeCurrentScreen();
    }

    public void shutdown()
    {
        saveConfiguration();
        components.forEach(SVComponent::shutdown);
    }

    public void crashClient()
    {
        W2K.getInstance().getVersionAbstractionLayer().execute(() ->
        {
            throw new Error("Intentionally crashed by Supervisor");
        });
    }

    public void shutdownForcefully()
    {
        W2K.getLogger().info("Shutting down forcefully!");
        System.exit(42069);
    }

    public void shutdownNuclear()
    {
        W2K.getLogger().info("TACTICAL NUKE, INCOMING!");
        Runtime.getRuntime().halt(1337);
    }

    public void shutdownSafely()
    {
        W2K.getInstance().getVersionAbstractionLayer().scheduleShutdown();
    }

    public List<String> dumpThreads()
    {
        List<String> all = new ArrayList<>();

        Arrays.stream(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)).forEach(thread ->
        {
            String header = "-- == ++ STACKTRACE DUMP - " + thread.getThreadName() + " ++ == --";
            String status = "STATUS: " + thread.getThreadState().name();
            String details = "DETAILS: " + String.format("Suspended: %s, Native: %s", thread.isSuspended() ? "Yes" : "No", thread.isInNative() ? "Yes" : "No");
            List<String> stacktrace = Arrays.stream(thread.getStackTrace()).map(element -> "    " + element.toString()).collect(Collectors.toList());

            all.add(header);
            all.add(status);
            all.add(details);
            all.addAll(stacktrace);
        });

        return all;
    }
}
