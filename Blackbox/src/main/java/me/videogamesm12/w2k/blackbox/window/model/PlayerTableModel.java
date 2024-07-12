package me.videogamesm12.w2k.blackbox.window.model;

import me.videogamesm12.w2k.blackbox.window.general.Dynamic;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import me.videogamesm12.w2k.supervisor.Supervisor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTableModel extends AbstractTableModel implements Dynamic
{
    private final List<String> columns = Arrays.asList("Username", "Display Name",  "UUID", "Ping (ms)");
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
        if (MinecraftClient.getInstance().world == null)
        {
            return;
        }

        rows.clear();

        rows.addAll(Supervisor.getInstance().getOnlinePlayers().stream().map(PlayerEntry::toTableRow).collect(Collectors.toList()));

        fireTableDataChanged();
    }
}
