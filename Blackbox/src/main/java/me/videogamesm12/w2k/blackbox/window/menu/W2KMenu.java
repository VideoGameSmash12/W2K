package me.videogamesm12.w2k.blackbox.window.menu;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.menu.w2k.DumpMenu;
import me.videogamesm12.w2k.blackbox.window.menu.w2k.ExperimentsMenu;
import me.videogamesm12.w2k.blackbox.window.menu.w2k.ModMenu;

import javax.swing.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class W2KMenu extends JMenu implements Dynamic
{
    private static final Queue<ModMenu<?>> modMenus = new ConcurrentLinkedQueue<>();
    //--
    private final JMenu hooksMenu = new JMenu("Hooks");

    public W2KMenu()
    {
        super("W2K");
        //--
        final DumpMenu dumpMenu = new DumpMenu();
        add(dumpMenu);
        final ExperimentsMenu experimentsMenu = new ExperimentsMenu();
        add(experimentsMenu);
        add(hooksMenu);
    }

    @Override
    public void update()
    {
        for (int i = 0; i < modMenus.size(); i++)
        {
            ModMenu<?> modMenu = modMenus.poll();
            hooksMenu.add(modMenu);
            SwingUtilities.updateComponentTreeUI(modMenu);
        }
    }

    public static void queueModMenu(ModMenu<?> mod)
    {
        modMenus.add(mod);
    }
}
