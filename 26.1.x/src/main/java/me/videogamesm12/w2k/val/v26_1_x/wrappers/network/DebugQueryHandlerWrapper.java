package me.videogamesm12.w2k.val.v26_1_x.wrappers.network;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.DataQueryHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQueryPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQueryPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(DebugQueryHandler.class)
public class DebugQueryHandlerWrapper implements DataQueryHandlerInterface
{
    @Unique
    private final Map<Integer, CompletableFuture<CompoundBinaryTag>> map = new HashMap<>();

    @Shadow
    @Final
    private ClientPacketListener connection;

    @Override
    public CompletableFuture<CompoundBinaryTag> w2k$queryEntity(int id)
    {
        // If we already have an active query, just go with that instead
        if (map.containsKey(id))
        {
            return map.get(id);
        }

        // Create the future
        final CompletableFuture<CompoundBinaryTag> future = new CompletableFuture<>();

        // Make sure it doesn't linger on forever
        future.orTimeout(10, TimeUnit.SECONDS);

        // Store the future so we can refer back to it later
        map.put(id, future);

        // Send the query
        connection.send(new ServerboundEntityTagQueryPacket(id, id));

        // Return the future
        return future;
    }

    @Override
    public CompletableFuture<CompoundBinaryTag> w2k$queryBlock(final BlockPosInterface pos)
    {
        // If we already have an active query, just go with that instead
        if (map.containsKey(pos.hashCode()))
        {
            return map.get(pos.hashCode());
        }

        // Create the future
        final CompletableFuture<CompoundBinaryTag> future = new CompletableFuture<>();

        // Make sure it doesn't linger on forever
        future.orTimeout(10, TimeUnit.SECONDS);

        // Store the future so we can refer back to it later
        map.put(pos.hashCode(), future);

        // Send the query
        connection.send(new ServerboundBlockEntityTagQueryPacket(pos.hashCode(), (BlockPos) pos));

        // Return the future
        return future;
    }

    @Override
    public void w2k$cancelAll()
    {
        map.forEach((_, future) -> future.completeExceptionally(new InterruptedException("Cancelled")));
    }

    @Inject(method = "handleResponse", at = @At("HEAD"), cancellable = true)
    public void handle(int transactionId, CompoundTag tag, CallbackInfoReturnable<Boolean> cir)
    {
        if (map.containsKey(transactionId))
        {
            final CompletableFuture<CompoundBinaryTag> compound = map.get(transactionId);
            compound.completeAsync(() -> W2K.getInstance().getVersionAbstractionLayer().nbt().nativeToAdventure(tag));
            map.remove(transactionId);
            cir.setReturnValue(true);
        }
    }
}
