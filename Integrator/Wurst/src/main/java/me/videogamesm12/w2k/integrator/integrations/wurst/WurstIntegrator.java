package me.videogamesm12.w2k.integrator.integrations.wurst;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.integrator.core.IModIntegrator;
import me.videogamesm12.w2k.integrator.core.IntegratorMetadata;
import me.videogamesm12.w2k.integrator.core.gui.PModCategoryMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.integrations.wurst.menu.WurstAltManagerDialog;
import me.videogamesm12.w2k.integrator.integrations.wurst.menu.WurstHackMenu;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.experiment.Experiment;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import net.wurstclient.Category;
import net.wurstclient.WurstClient;
import org.spongepowered.asm.mixin.Unique;

import javax.swing.*;
import java.util.Arrays;

@IntegratorMetadata(required = "wurst")
public class WurstIntegrator extends IModIntegrator
{
    @Unique
    private WurstAltManagerDialog altManagerDialog = null;

    @Override
    public void onStart()
    {
        // First things first, we add a warning to the logs to notify the user in the logs about the issue
        W2K.getLogger().warn("Greetings from W2K's developer - please do not report bugs/crashes caused by Wurst in this "
                + "instance unless you can verify that the issue happens even when W2K is not installed.");
        W2K.getLogger().warn("W2K has a module called 'Integrator' which injects code into Wurst to integrate it into the "
                + "Blackbox more cleanly, which changes its behavior and screws with the stacktraces. If you cannot "
                + "recreate a crash in an environment without W2K, you should instead report the issue on W2K's GitHub "
                + "at https://github.com/VideoGameSmash12/W2K/issues.");

        // Build a menu
        PModMenu<WurstClient> menu = new PModMenu<>("Wurst", WurstClient.INSTANCE);

        // Add the icon
        menu.addModIconIfPresent("wurst");

        // Adds the modules from all the categories as their own separate menus
        Arrays.stream(Category.values()).forEach(category -> {
            PModCategoryMenu categoryMenu = new PModCategoryMenu(category.getName());
            WurstClient.INSTANCE.getHax().getAllHax().stream().filter(hack -> hack.getCategory() == category)
                    .forEach(hack -> categoryMenu.addModule(new WurstHackMenu(hack)));
            menu.addSubMenu(categoryMenu);
        });

        if (ExperimentManager.isExperimentEnabled(Experiment.INTEGRATOR_WURST_ALT_MANAGER))
        {
            // Add a separator
            menu.addSeparator();

            // Add the Alt Manager option
            final JMenuItem altManager = new JMenuItem("Alt Manager");
            altManager.addActionListener(e ->
            {
                if (altManagerDialog == null)
                {
                    altManagerDialog = new WurstAltManagerDialog();
                }

                altManagerDialog.setVisible(true);
            });
            menu.add(altManager);
        }

        // Adds the final product
        W2KMenu.queueModMenu(menu);
    }
}
