package me.videogamesm12.w2k.val.v1_20_1.wrappers.network;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.DataQueryHandlerInterface;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
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

@Mixin(DataQueryHandler.class)
public class DataQueryHandlerWrapper implements DataQueryHandlerInterface
{
    private final Map<Integer, CompletableFuture<CompoundBinaryTag>> map = new HashMap<>();

    @Unique
    private boolean w2k$blocking;

    @Shadow
    private int expectedTransactionId;

    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Override
    public void w2k$setExpectedTransactionId(int value)
    {
        expectedTransactionId = value;
    }

    @Override
    public int w2k$getExpectedTransactionId()
    {
        return expectedTransactionId;
    }

    @Override
    public void w2k$blockQueries()
    {
        w2k$blocking = true;
    }

    @Override
    public void w2k$unblockQueries()
    {
        w2k$blocking = false;
    }

    @Override
    public boolean w2k$areQueriesBlocked()
    {
        return w2k$blocking;
    }

    @Override
    public CompletableFuture<CompoundBinaryTag> w2k$queryEntity(int id)
    {
        // Create the future
        final CompletableFuture<CompoundBinaryTag> future = new CompletableFuture<>();

        // Make sure it doesn't linger on forever
        future.orTimeout(10, TimeUnit.SECONDS);

        // Block future queries until this is done
        w2k$blockQueries();

        // Store the future so we can refer back to it later
        map.put(id, future);

        // Send the query
        networkHandler.sendPacket(new QueryEntityNbtC2SPacket(id, id));

        // Return the future
        return future;
    }

    @Override
    public void w2k$cancelAll()
    {
        map.forEach((id, future) -> future.completeExceptionally(new InterruptedException("Cancelled")));
    }

    @Inject(method = "handleQueryResponse", at = @At("HEAD"), cancellable = true)
    public void handle(int transactionId, NbtCompound nbt, CallbackInfoReturnable<Boolean> cir)
    {
        if (map.containsKey(transactionId))
        {
            final CompletableFuture<CompoundBinaryTag> compound = map.get(transactionId);
            compound.completeAsync(() -> W2K.getInstance().getVersionAbstractionLayer().nbt().nativeToAdventure(nbt));
            map.remove(transactionId);

            if (map.isEmpty())
            {
                w2k$unblockQueries();
            }

            cir.setReturnValue(true);
        }
    }
}
