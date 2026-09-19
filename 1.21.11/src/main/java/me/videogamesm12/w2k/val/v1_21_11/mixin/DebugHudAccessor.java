package me.videogamesm12.w2k.val.v1_21_11.mixin;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DebugHud.class)
public interface DebugHudAccessor
{
    @Accessor
    ChunkPos getPos();

    @Accessor
    void setPos(ChunkPos pos);

    @Invoker("resetChunk")
    void resetChunk();

    @Invoker("getWorld")
    World getWorld();

    @Invoker("getClientChunk")
    WorldChunk getClientChunk();

    @Invoker("getChunk")
    WorldChunk getChunk();
}
