/*
 * Copyright (c) 2022 Video
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

package me.videogamesm12.w2k.integrator.mixins.wurst;

import me.videogamesm12.w2k.blackbox.window.menu.W2KMenu;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.experiment.Experiments;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.integrator.core.gui.PModCategoryMenu;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.partitions.wurst.WurstAltManagerDialog;
import me.videogamesm12.w2k.integrator.partitions.wurst.WurstHackMenu;
import net.wurstclient.Category;
import net.wurstclient.WurstClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.swing.*;
import java.util.Arrays;

@Mixin(WurstClient.class)
public class WurstClientMixin
{
    @Unique
    private WurstAltManagerDialog altManagerDialog = null;

    @Inject(method = "initialize", at = @At(value = "INVOKE", target = "Lnet/wurstclient/altmanager/AltManager;<init>(Ljava/nio/file/Path;Ljava/nio/file/Path;)V", shift = At.Shift.AFTER), remap = false)
    public void injectInitialize(CallbackInfo ci)
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

        if (ExperimentManager.isExperimentEnabled(Experiments.INTEGRATOR_WURST_ALT_MANAGER))
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