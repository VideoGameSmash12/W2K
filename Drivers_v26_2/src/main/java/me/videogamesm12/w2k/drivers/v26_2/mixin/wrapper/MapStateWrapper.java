package me.videogamesm12.w2k.drivers.v26_2.mixin.wrapper;

import me.videogamesm12.w2k.kernel.data.IMapEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MapItemSavedData.class)
public class MapStateWrapper implements IMapEntry
{
    @Shadow
    @Final
    public byte scale;
    @Shadow
    @Final
    public ResourceKey<Level> dimension;
    @Shadow
    @Final
    public int centerX;
    @Shadow
    @Final
    public int centerZ;
    @Shadow
    @Final
    public boolean locked;
    @Shadow
    public byte[] colors;

    @Unique
    private String id = null;

    @Override
    public IMapEntry w2k$id(String id)
    {
        this.id = id;
        //--
        return this;
    }

    @Override
    public String w2k$id()
    {
        return id;
    }

    @Override
    public String w2k$scale()
    {
        return String.valueOf(scale);
    }

    @Override
    public String w2k$dimension()
    {
        return dimension.identifier().toString();
    }

    @Override
    public int w2k$centerX()
    {
        return centerX;
    }

    @Override
    public int w2k$centerZ()
    {
        return centerZ;
    }

    @Override
    public boolean w2k$locked()
    {
        return locked;
    }

    @Override
    public byte[] w2k$colors()
    {
        return colors;
    }

    @Override
    public String w2k$nbt()
    {
        return MapItemSavedData.CODEC.encodeStart(NbtOps.INSTANCE, MapItemSavedData.class.cast(this)).result()
                .orElse(new CompoundTag()).toString();
    }
}