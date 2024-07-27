package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.TileEntityTableModel;

import javax.swing.*;

public class TileEntitiesTab extends ScrollableTab
{
    public final JTable table;

    public TileEntitiesTab()
    {
        table = new JTable(new TileEntityTableModel());
        table.setCellSelectionEnabled(true);
        setup();
    }

    @Override
    public JComponent getContentComponent()
    {
        return table;
    }

    @Override
    public void update()
    {
        ((Dynamic) table.getModel()).update();
    }
}
