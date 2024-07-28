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

package me.videogamesm12.w2k.integrator.core.gui;

import me.videogamesm12.w2k.blackbox.window.menu.w2k.ModMenu;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.experiment.Experiments;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class PModMenu<T> extends ModMenu<T>
{
    private final T instance;

    public PModMenu(String name, T instance)
    {
        super(name, (Class<T>) instance.getClass());
        this.instance = instance;
    }

    @Override
    public T getModInstance()
    {
        return instance;
    }

    public void addSubMenu(PModSubMenu subMenu)
    {
        if (subMenu instanceof JMenu)
        {
            JMenu asMenu = (JMenu) subMenu;

            add(asMenu);
        }
    }

    public void addModIconIfPresent(String id)
    {
        if (ExperimentManager.isExperimentEnabled(Experiments.INTEGRATOR_MOD_ICONS))
        {
            FabricLoader.getInstance().getModContainer(id).flatMap(container -> container.getMetadata().getIconPath(128)).ifPresent(path ->
            {
                try (InputStream stream = getModInstance().getClass().getClassLoader().getResourceAsStream(path))
                {
                    if (stream != null)
                    {
                        setIcon(new ImageIcon(ImageIO.read(stream).getScaledInstance(24, 24, Image.SCALE_FAST)));
                    }
                }
                catch (IOException ignored)
                {
                }
            });
        }
    }
}
