package me.videogamesm12.w2k.blackbox.window.model;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedPlayerListEntry;
import me.videogamesm12.w2k.supervisor.Supervisor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerTableModel extends AbstractTableModel implements Dynamic
{
    private final List<String> columns = Arrays.asList("Username", "Display Name",  "UUID", "Ping (ms)");
    private final List<WrappedPlayerListEntry> entries = new ArrayList<>();

    @Override
    public String getColumnName(int column)
    {
        return columns.get(column);
    }

    @Override
    public int getRowCount()
    {
        return entries.size();
    }

    @Override
    public int getColumnCount()
    {
        return columns.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (rowIndex >= entries.size())
        {
            return null;
        }

        WrappedPlayerListEntry wrapped = entries.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
            {
                return wrapped.w2k$getPlayerName();
            }
            case 1:
            {
                return W2K.getInstance().getDriverManager().getVersionBridge().textToString(wrapped.w2k$getDisplayName());
            }
            case 2:
            {
                return wrapped.w2k$getPlayerUuid() != null ? Objects.requireNonNull(wrapped.w2k$getPlayerUuid()).toString() : "";
            }
            case 3:
            {
                return wrapped.w2k$getLatency();
            }
            default:
            {
                return null;
            }
        }
    }

    @Override
    public void update()
    {
        entries.clear();

        entries.addAll(Supervisor.getInstance().getOnlinePlayers());

        fireTableDataChanged();
    }
}
