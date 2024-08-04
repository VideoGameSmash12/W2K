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
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.diagnostics.PopulateCrashReportEvent;
import net.fabricmc.api.ClientModInitializer;

import java.util.*;
import java.util.stream.Collectors;

public class Integrator implements ClientModInitializer
{
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