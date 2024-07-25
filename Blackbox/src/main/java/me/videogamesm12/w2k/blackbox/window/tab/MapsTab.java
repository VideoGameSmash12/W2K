package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.blackbox.window.model.EntityTableModel;
import me.videogamesm12.w2k.blackbox.window.model.MapTableModel;
import me.videogamesm12.w2k.blackbox.window.model.enhanced.EnhancedMapTableModel;

import javax.swing.*;

public class MapsTab extends ScrollableTab
{
    public final JTable table;

    public MapsTab()
    {
        table = new JTable(Blackbox.getInstance().getConfig().isEnhancedListingEnabled() ?
                new EnhancedMapTableModel() : new MapTableModel());
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
