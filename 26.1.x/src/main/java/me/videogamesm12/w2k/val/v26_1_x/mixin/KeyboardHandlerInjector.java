package me.videogamesm12.w2k.val.v26_1_x.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.network.DataQueryHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.BlockPosInterface;
import me.videogamesm12.w2k.kernel.event.miscellaneous.KeyPressEvent;
import me.videogamesm12.w2k.kernel.event.network.DataQueryResponseEvent;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerInjector
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private final KeyPressEvent keyPressEvent = new KeyPressEvent();

    @Inject(method = "keyPress", at = @At("HEAD"))
    public void triggerKeyPressEvent(long handle, int action, KeyEvent event, CallbackInfo ci)
    {
        if (handle == minecraft.getWindow().handle())
        {
            W2K.getEventBus().post(keyPressEvent.update(event.modifiers(), event.key()));
        }
    }

    @WrapOperation(method = "copyRecreateCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DebugQueryHandler;queryEntityTag(ILjava/util/function/Consumer;)V"))
    public void parallelCopyEntity(DebugQueryHandler instance, int entityId, Consumer<CompoundTag> callback, Operation<Void> original, @Local(name = "entity") Entity entity, @Local(name = "id") Identifier id)
    {
        final DataQueryHandlerInterface handler = (DataQueryHandlerInterface) instance;
        handler.w2k$queryEntity(entityId)
                .whenComplete((result, exception) ->
                {
                    if (exception != null)
                    {
                        if (exception instanceof TimeoutException)
                        {
                            W2K.getLogger().warn("Timed out whilst attempting to query entity {}", entityId);
                            return;
                        }

                        W2K.getLogger().error("Failed to query entity {}", entityId, exception);
                        return;
                    }

                    callback.accept((CompoundTag) handler.w2k$val().nbt().adventureToNative(result));
                    W2K.getEventBus().post(new DataQueryResponseEvent(
                            id.toString(),
                            (BlockPosInterface) entity.blockPosition(),
                            result,
                            "entity",
                            "minecraft:debug_query"));
                });
    }

    @WrapOperation(method = "copyRecreateCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DebugQueryHandler;queryBlockEntityTag(Lnet/minecraft/core/BlockPos;Ljava/util/function/Consumer;)V"))
    public void parallelCopyBlock(DebugQueryHandler instance, BlockPos blockPos, Consumer<CompoundTag> callback, Operation<Void> original, @Local(name = "state") BlockState state)
    {
        final DataQueryHandlerInterface handler = (DataQueryHandlerInterface) instance;
        handler.w2k$queryBlock((BlockPosInterface) blockPos)
                .whenComplete((result, exception) ->
                {
                    if (exception != null)
                    {
                        if (exception instanceof TimeoutException)
                        {
                            W2K.getLogger().warn("Timed out whilst attempting to query block {}", ((BlockPosInterface) blockPos).w2k$toString());
                            return;
                        }

                        W2K.getLogger().error("Failed to query block {}", ((BlockPosInterface) blockPos).w2k$toString(), exception);
                        return;
                    }

                    callback.accept((CompoundTag) handler.w2k$val().nbt().adventureToNative(result));
                    W2K.getEventBus().post(new DataQueryResponseEvent(
                            state.typeHolder().unwrapKey().map(value -> value.identifier().toString()).orElse("unknown"),
                            (BlockPosInterface) blockPos,
                            result,
                            "block",
                            "minecraft:debug_query"));
                });

    }
}
