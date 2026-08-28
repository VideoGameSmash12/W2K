package me.videogamesm12.w2k.drivers.v1_20_1.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.toolbox.modules.QueryLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.block.BlockState;
import net.minecraft.client.Keyboard;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin
{
    @Inject(method = "copyEntity", at = @At("HEAD"))
    public void onCopyEntity(Identifier id, Vec3d pos, NbtCompound nbt, CallbackInfo ci)
    {
        final QueryLogger module = W2K.getInstance().getModuleManager().getModule(QueryLogger.class);
        if (module.isEnabled())
        {
            module.logQueryResult(id.toString(), nbt.toString()).whenComplete((result, error) ->
            {
                if (error != null)
                {
                    W2K.getLogger().error("Failed to dump queried entity", error);
                }
                else
                {
                    final String path = result.getSuccessful().get(0);
                    W2K.getLogger().info("Successfully dumped entity query response to {}", path);

                    if (module.alert.get())
                    {
                        W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(Component.text("Entity query response logged to " + path + ".", NamedTextColor.GREEN));
                    }
                }
            });
        }
    }

    @Inject(method = "copyBlock", at = @At("HEAD"))
    public void onCopyBlock(BlockState state, BlockPos pos, NbtCompound nbt, CallbackInfo ci)
    {
        final QueryLogger module = W2K.getInstance().getModuleManager().getModule(QueryLogger.class);
        if (module.isEnabled())
        {
            module.logQueryResult(state.getRegistryEntry().getKey().map(value -> value.getValue().toString()).orElse("air"), nbt.toString()).whenComplete((result, error) ->
            {
                if (error != null)
                {
                    W2K.getLogger().error("Failed to dump queried block", error);
                }
                else
                {
                    final String path = result.getSuccessful().get(0);
                    W2K.getLogger().info("Successfully dumped block query response to {}", path);

                    if (module.alert.get())
                    {
                        W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(Component.text("Block query response logged to " + path + ".", NamedTextColor.GREEN));
                    }
                }
            });
        }
    }
}
