package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.EntityTableModel;

import javax.swing.*;

public class EntitiesTab extends ScrollableTab
{
    public final JTable table;

    public EntitiesTab()
    {
        table = new JTable(new EntityTableModel());
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
