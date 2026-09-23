package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.MapTableModel;

import javax.swing.*;

public class MapsTab extends ScrollableTab
{
    public final JTable table;

    public MapsTab()
    {
        table = new JTable(new MapTableModel(Blackbox.getInstance().getConfig().isEnhancedListingEnabled()));
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
