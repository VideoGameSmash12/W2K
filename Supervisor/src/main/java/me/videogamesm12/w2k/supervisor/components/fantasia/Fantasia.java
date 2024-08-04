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

package me.videogamesm12.w2k.supervisor.components.fantasia;

import lombok.Getter;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.SVComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Fantasia</h1>
 * <p>A component for the Supervisor that allows you to interface with it with a Telnet client.</p>
 */
public class Fantasia implements SVComponent
{
    @Getter
    private static final Logger serverLogger = LogManager.getLogger("Fantasia-Server");
    @Getter
    private static Fantasia instance;
    //--
    @Getter
    private Server server;

    @Override
    public String identifier()
    {
        return "Fantasia";
    }

    @Override
    public void setup()
    {
        instance = this;
        //--
        serverLogger.info("Starting Fantasia server...");
        server = new Server();
        server.start();
    }

    @Override
    public void shutdown()
    {
        server.shutdown();
    }

    @Override
    public List<String> crashReportDetails()
    {
        final List<String> lines = new ArrayList<>();
        lines.add("\tActive Sessions:");
        if (!server.getSessions().isEmpty())
        {
            lines.addAll(server.getSessions().stream()
                    .map(session -> "\t\t" + session.getConnectionIdentifier()).collect(Collectors.toList()));
        }
        else
        {
            lines.add("\t\t(none)");
        }
        lines.add("\tConfiguration:");
        Supervisor.getConfig().getFantasiaSettings().getSettings()
                .forEach((name, value) -> lines.add("\t\t" + name + ": " + value));
        return lines;
    }
}