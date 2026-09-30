package me.videogamesm12.w2k.kernel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.command.WCommandManager;
import me.videogamesm12.w2k.kernel.commands.TestCmd;
import me.videogamesm12.w2k.kernel.commands.W2KCmd;
import me.videogamesm12.w2k.kernel.communication.WCommunicationManager;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.driver.WDriverManager;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.module.WModuleManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class W2K implements ModInitializer
{
    @Getter
    private static W2K instance;

    @Getter
    private static final EventBus eventBus = new EventBus();
    @Getter
    private static final Logger logger = LogManager.getLogger("W2K");
    @Getter
    private static final File modFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "w2k");

    static
    {
        if (!modFolder.isDirectory())
        {
            modFolder.mkdirs();
        }
    }

    private BaseVersionAbstractionLayer<?> versionAbstractionLayer;
    @Getter
    private WDriverManager driverManager;
    @Getter
    private WCommandManager commandManager;
    @Getter
    private WCommunicationManager communicationManager;
    @Getter
    private WModuleManager moduleManager;

    @Override
    public void onInitialize()
    {
        instance = this;

        logger.info("Setting up version abstraction layer");
        installVersionAbstractionLayer();

        logger.info("Setting up command manager");
        commandManager = new WCommandManager();
        logger.info("Setting up communication manager");
        communicationManager = new WCommunicationManager();
        logger.info("Setting up module manager");
        moduleManager = new WModuleManager();
        logger.info("Setting up driver manager");
        driverManager = new WDriverManager();
        logger.info("Kernel successfully initialized");

        logger.info("Registering commands");
        commandManager.registerCommand(W2KCmd.class);
        commandManager.registerCommand(TestCmd.class);

        logger.info("Loading drivers");
        driverManager.loadDrivers();
        logger.info("Setting up drivers");
        driverManager.driversByMod().forEach((mod, drivers) ->
        {
            drivers.forEach(driver ->
            {
                try
                {
                    driver.init();
                }
                catch (Throwable ex)
                {
                    W2K.getLogger().error("Driver {} failed to initialize, skipping", driver.getClass().getName(), ex);
                    return;
                }

                driver.commands().forEach(commandManager::registerCommand);
                driver.modules().forEach(module -> moduleManager.registerModule(mod, module));
            });
        });
        logger.info("Drivers successfully set up");

        // Experiment
        if (!ExperimentManager.getEnabledExperiments().isEmpty())
        {
            logger.warn("[!] Experiments have been enabled. Expect some instability. List of enabled experiments:");
            ExperimentManager.getEnabledExperiments().forEach(experiment -> logger.warn("[!]  - {}", experiment.getIdentifier()));
        }

        logger.info("Loading module configuration");
        moduleManager.loadModules();
        logger.info("Modules successfully configured");

        getEventBus().register(this);
    }

    @SuppressWarnings("unchecked")
    public <Minecraft> BaseVersionAbstractionLayer<Minecraft> getVersionAbstractionLayer()
    {
        if (versionAbstractionLayer == null)
        {
            installVersionAbstractionLayer();
        }

        return (BaseVersionAbstractionLayer<Minecraft>) versionAbstractionLayer;
    }

    private void installVersionAbstractionLayer()
    {
        versionAbstractionLayer = FabricLoader.getInstance().getEntrypoints("w2k-version-abstraction-layer", BaseVersionAbstractionLayer.class).stream()
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Unable to find a version abstraction layer compatible with this version of the game"));

        versionAbstractionLayer.setup();
    }

    @Subscribe
    public void onCrashReport(PopulateCrashReportEvent event)
    {
        // Add our build information
        event.appendSection("Build", "Details:\r\n" +
                Objects.requireNonNull(BuildMetadata.getMetadataFromMod("w2k")).toCrashReportSection());

        // Append our loaded drivers
        final StringBuilder driverList = new StringBuilder();
        if (!driverManager.getDrivers().isEmpty())
        {
            driverList.append("\nDrivers:\n");
            driverManager.getDrivers().forEach((id, instance) ->
                    driverList.append("\t").append(instance.getClass().getName()).append(" (")
                            .append("registered under ").append(id).append(")"));
        }
        event.appendSection("Driver Manager", driverList.toString());

        // Append our loaded commands
        final StringBuilder commandList = new StringBuilder();
        commandList.append("Registered Commands:\n");
        commandManager.getCommands().forEach(command -> commandList.append("\t").append(command.getName())
                .append(" (class ").append(command.getClass().getName()).append(")\n"));
        event.appendSection("Command Manager", commandList.toString());

        // Append our enabled experiments (if any)
        if (!ExperimentManager.getEnabledExperiments().isEmpty())
        {
            List<String> experimentManager = new ArrayList<>();
            experimentManager.add("Enabled:");
            experimentManager.addAll(ExperimentManager.getEnabledExperiments().stream()
                    .map(experiment -> "\t" + experiment.getIdentifier()).collect(Collectors.toList()));
            event.appendSection("Experiment Manager", experimentManager.toArray(new String[0]));
        }
    }

    @Subscribe
    public void onShutdown(ClientStoppedEvent event)
    {
        // Save our module settings
       if (moduleManager != null)
       {
           logger.info("Saving module settings");
           moduleManager.saveModules();
       }

        WCommand.cancelAllScheduledOperations();
    }
}
