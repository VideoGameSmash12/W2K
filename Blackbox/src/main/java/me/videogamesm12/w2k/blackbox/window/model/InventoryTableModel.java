package me.videogamesm12.w2k.blackbox.window.model;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.supervisor.Supervisor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryTableModel extends AbstractTableModel implements Dynamic
{
    private final List<String> columns = Arrays.asList("Name", "Type", "Count", "Damage", "Slot");
    private final List<List<Object>> rows = new ArrayList<>();

    @Override
    public String getColumnName(int column)
    {
        return columns.get(column);
    }

    @Override
    public int getRowCount()
    {
        return rows.size();
    }

    @Override
    public int getColumnCount()
    {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return rows.get(rowIndex).get(columnIndex);
    }

    @Override
    public void update()
    {
        rows.clear();

        rows.addAll(Supervisor.getInstance().getInventory().stream().filter(IItemStackEntry::w2k$isNotEmpty)
                .map(IItemStackEntry::w2k$toTableRow).collect(Collectors.toList()));

        fireTableDataChanged();
    }
}
