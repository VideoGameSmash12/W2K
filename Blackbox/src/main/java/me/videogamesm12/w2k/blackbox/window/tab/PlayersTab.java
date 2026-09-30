package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.PlayerTableModel;

import javax.swing.*;
import java.awt.*;

public class PlayersTab extends ScrollableTab
{
    private final JTable table;

    public PlayersTab()
    {
        table = new JTable(new PlayerTableModel(Blackbox.getInstance().getConfig().isEnhancedListingEnabled()));
        table.setCellSelectionEnabled(true);
        setup();
    }

    @Override
    public Component getContentComponent()
    {
        return table;
    }

    @Override
    public void update()
    {
        ((Dynamic) table.getModel()).update();
    }
}
