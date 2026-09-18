package me.videogamesm12.w2k.val.v1_20_1.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.DataQueryHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import me.videogamesm12.w2k.kernel.event.miscellaneous.KeyPressEvent;
import me.videogamesm12.w2k.kernel.event.miscellaneous.PanicKeyCombinationEvent;
import me.videogamesm12.w2k.kernel.event.network.DataQueryResponseEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Mixin(Keyboard.class)
public class KeyboardInjector
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private final KeyPressEvent keyPressEvent = new KeyPressEvent();

    @Inject(method = "onKey", at = @At("HEAD"))
    public void triggerKeyPressEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci)
    {
        if (window == client.getWindow().getHandle())
        {
            W2K.getEventBus().post(keyPressEvent.update(modifiers, key));
        }
    }

    @WrapOperation(method = "copyLookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/DataQueryHandler;queryEntityNbt(ILjava/util/function/Consumer;)V"))
    public void parallelCopyEntity(DataQueryHandler instance, int entityNetworkId, Consumer<NbtCompound> callback, Operation<Void> original, @Local Entity entity, @Local Identifier identifier)
    {
        final DataQueryHandlerInterface handler = (DataQueryHandlerInterface) instance;
        handler.w2k$queryEntity(entityNetworkId)
                .whenComplete((result, exception) ->
                {
                    if (exception != null)
                    {
                        if (exception instanceof TimeoutException)
                        {
                            W2K.getLogger().warn("Timed out whilst attempting to query entity {}", entityNetworkId);
                            return;
                        }

                        W2K.getLogger().error("Failed to query entity {}", entityNetworkId, exception);
                        return;
                    }

                    callback.accept((NbtCompound) handler.w2k$val().nbt().adventureToNative(result));
                    W2K.getEventBus().post(new DataQueryResponseEvent(
                            identifier.toString(),
                            (BlockPosInterface) entity.getBlockPos(),
                            result,
                            "entity",
                            "minecraft:debug_query"));
                });
    }

    @WrapOperation(method = "copyLookAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/DataQueryHandler;queryBlockNbt(Lnet/minecraft/util/math/BlockPos;Ljava/util/function/Consumer;)V"))
    public void parallelCopyBlock(DataQueryHandler instance, BlockPos pos, Consumer<NbtCompound> callback, Operation<Void> original, @Local BlockState state)
    {
        final DataQueryHandlerInterface handler = (DataQueryHandlerInterface) instance;
        handler.w2k$queryBlock((BlockPosInterface) pos)
                .whenComplete((result, exception) ->
                {
                    if (exception != null)
                    {
                        if (exception instanceof TimeoutException)
                        {
                            W2K.getLogger().warn("Timed out whilst attempting to query block {}", ((BlockPosInterface) pos).w2k$toString());
                            return;
                        }

                        W2K.getLogger().error("Failed to query block {}", ((BlockPosInterface) pos).w2k$toString(), exception);
                        return;
                    }

                    callback.accept((NbtCompound) handler.w2k$val().nbt().adventureToNative(result));
                    W2K.getEventBus().post(new DataQueryResponseEvent(
                            state.getRegistryEntry().getKey().map(value -> value.getValue().toString()).orElse("unknown"),
                            (BlockPosInterface) pos,
                            result,
                            "block",
                            "minecraft:debug_query"));
                });

    }
}
