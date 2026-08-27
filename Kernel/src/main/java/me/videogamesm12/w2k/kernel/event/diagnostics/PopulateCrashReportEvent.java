package me.videogamesm12.w2k.kernel.event.diagnostics;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.util.Arrays;

/**
 * <h1>PopulateCrashReportEvent</h1>
 * <p>Event that is called while the game is generating a crash report.</p>
 */
@Getter
public class PopulateCrashReportEvent extends CustomEvent
{
    private final StringBuilder details = new StringBuilder();

    /**
     * Append a new section to W2K's dedicated crash report section.
     * @param header    The name of the section's header
     * @param entries   A series of strings for each line
     */
    public void appendSection(String header, String... entries)
    {
        details.append("[").append(header).append("]\n");
        Arrays.stream(entries).forEach(s -> details.append(s).append("\n"));
        details.append("\n");
    }
}
