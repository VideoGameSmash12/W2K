package me.videogamesm12.w2k.kernel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.videogamesm12.w2k.kernel.command.WCommandManager;
import me.videogamesm12.w2k.kernel.commands.ExperimentsCmd;
import me.videogamesm12.w2k.kernel.commands.W2KCmd;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.driver.WDriverManager;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.util.VersionUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
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

    @Getter
    private WDriverManager driverManager;
    @Getter
    private WCommandManager commandManager;

    @Override
    public void onInitialize()
    {
        instance = this;

        logger.info("Setting up driver manager");
        driverManager = new WDriverManager();
        logger.info("Setting up command manager");
        commandManager = new WCommandManager();
        logger.info("Kernel successfully initialized");

        logger.info("Loading required drivers");
        driverManager.loadRequiredDrivers();

        logger.info("Loading optional drivers");
        driverManager.loadOptionalDrivers();

        logger.info("We are running version {}!", driverManager.getVersionFetcher().getGameVersion());
        commandManager.registerCommand(W2KCmd.class);
        commandManager.registerCommand(ExperimentsCmd.class);

        // Experiment
        if (!ExperimentManager.getEnabledExperiments().isEmpty())
        {
            logger.warn("[!] Experiments have been enabled. Expect some instability. List of enabled experiments:");
            ExperimentManager.getEnabledExperiments().forEach(experiment -> logger.warn("[!]  - {}", experiment.name()));
        }

        getEventBus().register(this);
    }

    @Subscribe
    public void onCrashReport(PopulateCrashReportEvent event)
    {
        // Add our build information
        event.appendSection("Kernel Build", "Details:\r\n" +
                Objects.requireNonNull(BuildMetadata.getMetadataFromClassJar(W2K.class)).toCrashReportSection());

        // Append our loaded drivers
        final StringBuilder driverList = new StringBuilder();
        driverList.append("Primary Drivers:\n");
        driverList.append("\tWCommandDriver: ").append(driverManager.getCommandWrapper() != null ?
                        driverManager.getCommandWrapper().getClass().getName() : "(not loaded)").append("\n");
        driverList.append("\tWEventPassThruDriver: ").append(driverManager.getEventPassThru() != null ?
                driverManager.getEventPassThru().getClass().getName() : "(not loaded)").append("\n");
        driverList.append("\tWVersionBridgeDriver: ").append(driverManager.getVersionBridge() != null ?
                driverManager.getVersionBridge().getClass().getName() : "(not loaded)").append("\n");
        driverList.append("\tWVersionFetcherDriver: ").append(driverManager.getVersionFetcher() != null ?
                driverManager.getVersionFetcher().getClass().getName() : "(not loaded)");
        if (!driverManager.getOptionalDrivers().isEmpty())
        {
            driverList.append("\nOptional Drivers:\n");
            driverManager.getOptionalDrivers().forEach((id, instance) ->
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
                    .map(experiment -> "\t" + experiment.name()).collect(Collectors.toList()));
            event.appendSection("Experiment Manager", experimentManager.toArray(new String[0]));
        }
    }
}
