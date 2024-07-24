package me.videogamesm12.w2k.kernel.event.lifecycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

import java.io.File;

@Getter
@Setter
@RequiredArgsConstructor
public class ClientCrashedEvent extends CustomEvent
{
    private final Object client;

    private final Throwable cause;

    private final File crashReportFile;
}
