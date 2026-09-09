package me.videogamesm12.w2k.blackbox2.event;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.blackbox2.Blackbox;
import me.videogamesm12.w2k.blackbox2.gui.main.MainWindow;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

@RequiredArgsConstructor
public class BlackboxOpenedEvent extends CustomEvent
{
    private final Blackbox instance;
    private final MainWindow window;
}
