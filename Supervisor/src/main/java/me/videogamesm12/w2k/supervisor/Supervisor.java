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
import me.videogamesm12.w2k.kernel.data.*;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.supervisor.api.SVComponent;
import me.videogamesm12.w2k.supervisor.components.fantasia.Fantasia;
import me.videogamesm12.w2k.supervisor.components.flags.Flags;
import me.videogamesm12.w2k.supervisor.components.watchdog.Watchdog;
import net.fabricmc.loader.api.FabricLoader;

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
        W2K.getInstance().getDriverManager().getVersionBridge().sendMessage(message);
    }

    public void disconnect()
    {
        W2K.getInstance().getDriverManager().getVersionBridge().disconnect();
    }

    public void runCommand(String command)
    {
        W2K.getInstance().getDriverManager().getVersionBridge().runCommand(command);
    }

    public List<PlayerEntry> getOnlinePlayers()
    {
        return W2K.getInstance().getDriverManager().getVersionBridge().getOnlinePlayers();
    }

    public List<EntityEntry> getNearbyEntities()
    {
        return W2K.getInstance().getDriverManager().getVersionBridge().getNearbyEntities();
    }

    public List<TileEntry> getNearbyTileEntities()
    {
        return W2K.getInstance().getDriverManager().getVersionBridge().getNearbyTileEntities();
    }

    public List<MapEntry> getLoadedMaps()
    {
        return W2K.getInstance().getDriverManager().getVersionBridge().getLoadedMaps();
    }

    public List<InventoryEntry> getInventory()
    {
        return W2K.getInstance().getDriverManager().getVersionBridge().getInventory();
    }

    public void closeCurrentScreen()
    {
        W2K.getInstance().getDriverManager().getVersionBridge().closeCurrentScreen();
    }

    public void shutdown()
    {
        saveConfiguration();
        components.forEach(SVComponent::shutdown);
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
        W2K.getInstance().getDriverManager().getVersionBridge().scheduleSafeShutdown();
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
