package me.videogamesm12.w2k.blackbox.window.model;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import me.videogamesm12.w2k.supervisor.Supervisor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTableModel extends AbstractTableModel implements Dynamic
{
    private final List<String> columns;
    private final List<List<Object>> rows = new ArrayList<>();

    public PlayerTableModel(boolean enhanced)
    {
        this.columns = enhanced ?
                Arrays.asList("Username", "Display Name",  "UUID", "Ping (ms)", "Gamemode", "Model", "Skin ID") :
                Arrays.asList("Username", "Display Name",  "UUID", "Ping (ms)");
    }

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

        rows.addAll(Supervisor.getInstance().getPlayerList().stream()
                .map(PlayerListEntryInterface::w2k$toTableRow)
                .collect(Collectors.toList()));

        fireTableDataChanged();
    }
}
