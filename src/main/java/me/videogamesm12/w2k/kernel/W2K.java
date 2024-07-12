package me.videogamesm12.w2k.kernel;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.videogamesm12.w2k.kernel.driver.WDriverManager;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private WDriverManager driverManager;

    @Override
    public void onInitialize()
    {
        instance = this;

        logger.info("Setting up driver manager");
        driverManager = new WDriverManager();
        logger.info("Kernel successfully initialized");

        logger.info("Loading required drivers");
        driverManager.loadRequiredDrivers();

        logger.info("We are running version " + driverManager.getVersionFetcher().getGameVersion() + "!");
    }
}
