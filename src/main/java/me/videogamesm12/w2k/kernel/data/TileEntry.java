package me.videogamesm12.w2k.kernel.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class TileEntry
{
    private final String type;

    private final int x;

    private final int y;

    private final int z;

    private final String data;

    public List<Object> toTableRow()
    {
        return Arrays.asList(type, String.format("%s, %s, %s", x, y, z), data);
    }
}
