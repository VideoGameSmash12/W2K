package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.InventoryTableModel;

import javax.swing.*;

public class InventoryTab extends ScrollableTab
{
    public final JTable table;

    public InventoryTab()
    {
        table = new JTable(new InventoryTableModel(Blackbox.getInstance().getConfig().isEnhancedListingEnabled()));
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
