package me.videogamesm12.w2k.blackbox.window.menu;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.menu.w2k.ModMenu;
import me.videogamesm12.w2k.blackbox.window.menu.w2k.ModulesMenu;

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
        add(new ModulesMenu());
        add(hooksMenu);
    }

    @Override
    public void update()
    {
        for (int i = 0; i < modMenus.size(); i++)
        {
            hooksMenu.add(modMenus.poll());
        }
    }

    public static void queueModMenu(ModMenu<?> mod)
    {
        modMenus.add(mod);
    }
}
