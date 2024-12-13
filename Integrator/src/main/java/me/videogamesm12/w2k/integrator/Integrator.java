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

package me.videogamesm12.w2k.integrator;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.integrator.core.IModIntegrator;
import me.videogamesm12.w2k.integrator.core.IntegratorMetadata;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.*;
import java.util.stream.Collectors;

public class Integrator implements ClientModInitializer
{
    private final List<IModIntegrator> integrators = new ArrayList<>();

    @Getter
    private static final Map<String, EventBus> eventTransit = new HashMap<>();

    public static EventBus getModEventBus(String mod)
    {
        if (!eventTransit.containsKey(mod)) eventTransit.put(mod, new EventBus());

        return eventTransit.get(mod);
    }

    @Override
    public void onInitializeClient()
    {
        W2K.getEventBus().register(this);

        integrators.addAll(FabricLoader.getInstance().getEntrypoints("w2k-integrator-mod-hooks", IModIntegrator.class).stream().filter(entry ->
        {
            if (!entry.getClass().isAnnotationPresent(IntegratorMetadata.class))
            {
                return false;
            }

            final IntegratorMetadata metadata = entry.getClass().getAnnotation(IntegratorMetadata.class);

            // There are two methods that one can take here to determine whether to use an integrator. Integrations for
            //  Fabric mods can just specify their mod ID as the "required" parameter. For mods loaded using *other
            //  means*, they have to specify a class that gets checked on runtime to see if the mod is loaded.
            //
            // Examples of valid use cases for both:
            //  - Litematica supports different versions of Minecraft using different mod loaders (Rift for 1.13.2 and
            //  Fabric for 1.14+), but we want to integrate into the mod regardless of how it's loaded in, so we use the
            //  requiredClass parameter with the main entrypoint class so that if we're loaded in 1.13.2 and the player
            //  uses a mod to get the Rift mod loader working with Litematica, it'll still integrate just fine.
            //
            //  - ViaFabricPlus only supports Fabric, so we would take the simpler approach of giving the mod ID and let
            //  it handle the rest.
            return (metadata.required().length > 0 && Arrays.stream(metadata.required()).allMatch(e -> FabricLoader.getInstance().isModLoaded(e))
                    || metadata.requiredClasses().length > 0 && Arrays.stream(metadata.requiredClasses()).allMatch(e -> {
                try
                {
                    Class.forName(e);
                    return true;
                }
                catch (ClassNotFoundException ex)
                {
                    return false;
                }
            })) && Arrays.stream(metadata.breaks()).noneMatch(e -> FabricLoader.getInstance().isModLoaded(e));
        }).collect(Collectors.toList()));

        integrators.forEach(IModIntegrator::setup);
    }

    @Subscribe
    public void onCrashReport(PopulateCrashReportEvent event)
    {
        final List<String> section = new ArrayList<>(Collections.singletonList("Integrated Mods:"));
        section.addAll(eventTransit.keySet().stream().map(name -> "\t" + name.replace("integrator:", ""))
                .collect(Collectors.toList()));
        event.appendSection("Integrator", section.toArray(new String[0]));
    }
}