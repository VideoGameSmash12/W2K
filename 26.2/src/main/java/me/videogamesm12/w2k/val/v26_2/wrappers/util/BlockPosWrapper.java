package me.videogamesm12.w2k.val.v26_2.wrappers.util;

import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPos.class)
public class BlockPosWrapper implements BlockPosInterface
{
    @Override
    public int w2k$x()
    {
        return Vec3i.class.cast(this).getX();
    }

    @Override
    public int w2k$y()
    {
        return Vec3i.class.cast(this).getY();
    }

    @Override
    public int w2k$z()
    {
        return Vec3i.class.cast(this).getZ();
    }
}
