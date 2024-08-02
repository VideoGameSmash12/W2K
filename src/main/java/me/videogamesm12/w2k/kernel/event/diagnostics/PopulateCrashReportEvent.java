package me.videogamesm12.w2k.kernel.event.diagnostics;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.util.Arrays;

@Getter
public class PopulateCrashReportEvent extends CustomEvent
{
    private final StringBuilder details = new StringBuilder();

    public void appendSection(String header, String... entries)
    {
        details.append("[").append(header).append("]\n");
        Arrays.stream(entries).forEach(s -> details.append(s).append("\n"));
        details.append("\n");
    }
}
