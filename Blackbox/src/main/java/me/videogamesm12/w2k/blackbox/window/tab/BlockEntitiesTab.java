package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.BlockEntityTableModel;

import javax.swing.*;

public class BlockEntitiesTab extends ScrollableTab
{
    public final JTable table;

    public BlockEntitiesTab()
    {
        table = new JTable(new BlockEntityTableModel());
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
