package me.videogamesm12.w2k.drivers.v26_1.mixin.wrapper;

import me.videogamesm12.w2k.kernel.data.IMapEntry;

import java.util.List;

public class MapStateWrapper implements IMapEntry
{
    @Override
    public String w2k$id() {
        return "";
    }

    @Override
    public String w2k$scale() {
        return "";
    }

    @Override
    public String w2k$dimension() {
        return "";
    }

    @Override
    public int w2k$centerX() {
        return 0;
    }

    @Override
    public int w2k$centerZ() {
        return 0;
    }

    @Override
    public boolean w2k$locked() {
        return false;
    }

    @Override
    public byte[] w2k$colors() {
        return new byte[0];
    }

    @Override
    public String w2k$nbt() {
        return "";
    }

    @Override
    public List<Object> w2k$toTableRow() {
        return List.of();
    }
}
