package me.videogamesm12.w2k.kernel.event.lifecycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.io.File;

/**
 * <h1>ClientCrashedEvent</h1>
 * <p>Event that is called right before the game closes itself after crashing.</p>
 */
@Getter
@Setter
@RequiredArgsConstructor
public class ClientCrashedEvent extends CustomEvent
{
    /**
     * An object representing the {@link net.minecraft.client.MinecraftClient} instance.
     */
    private final Object client;

    /**
     * The exception that caused the crash.
     */
    private final Throwable cause;

    /**
     * The automatically generated crash report file.
     */
    private final File crashReportFile;
}
