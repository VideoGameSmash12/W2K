package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.PropertiesTableModel;

import javax.swing.*;

public class PropertiesTab extends ScrollableTab
{
    public final JTable table;

    public PropertiesTab()
    {
        table = new JTable(new PropertiesTableModel());
        table.setCellSelectionEnabled(true);
        table.setAutoCreateRowSorter(true);
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
