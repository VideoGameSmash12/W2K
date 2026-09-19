package me.videogamesm12.w2k.val.v1_21_11.wrappers.world;

import me.videogamesm12.w2k.kernel.abstraction.world.MapStateInterface;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MapState.class)
public abstract class MapStateWrapper implements MapStateInterface
{
    @Shadow
    @Final
    public byte scale;
    @Shadow
    @Final
    public RegistryKey<World> dimension;
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
    public MapStateInterface w2k$id(String id)
    {
        this.id = id;
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
        return dimension.getValue().toString();
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
        return MapState.CODEC.encodeStart(NbtOps.INSTANCE, MapState.class.cast(this)).result()
                .orElse(new NbtCompound()).toString();
    }
}
