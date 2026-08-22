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

package me.videogamesm12.w2k.blackbox.theming;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ThemeRegistry
{
    @Getter
    private static final Map<String, ITheme> themes = new HashMap<>();

    @Getter
    private static final List<IThemeType> themeTypes = new ArrayList<>();

    public static void setupThemes()
    {
        final Map<ModContainer, AtomicInteger> themeTypesPerMod = new HashMap<>();
        final Map<ModContainer, AtomicInteger> themesPerMod = new HashMap<>();

        FabricLoader.getInstance().getEntrypointContainers("w2k-blackbox", IThemeProvider.class).forEach(container ->
        {
            final IThemeProvider themeProvider = container.getEntrypoint();
            final ModContainer modContainer = container.getProvider();
            //--
            if (!themeTypesPerMod.containsKey(modContainer)) themeTypesPerMod.put(modContainer, new AtomicInteger());
            if (!themesPerMod.containsKey(modContainer)) themesPerMod.put(modContainer, new AtomicInteger());
            //--
            final AtomicInteger typeCount = themeTypesPerMod.get(container.getProvider());
            final AtomicInteger themeCount = themesPerMod.get(container.getProvider());
            //--
            try
            {
                final List<IThemeType> types = themeProvider.getTypes();
                themeTypes.addAll(types);
                typeCount.addAndGet(types.size());
                //--
                final Map<String, ITheme> modThemes = themeProvider.getThemes();
                themes.putAll(modThemes);
                themeCount.addAndGet(modThemes.size());
            }
            catch (Throwable ex)
            {
                W2K.getLogger().error("Failed to register themes from mod {}", container.getProvider().getMetadata().getName(), ex);
            }
        });

        themesPerMod.keySet().forEach(container ->
                W2K.getLogger().info("Loaded {} theme types and {} themes from mod {}",
                        themeTypesPerMod.get(container).get(),
                        themesPerMod.get(container).get(),
                        container.getMetadata().getName()));
    }

    public static ITheme getTheme(String id)
    {
        return themes.getOrDefault(id, null);
    }
}
