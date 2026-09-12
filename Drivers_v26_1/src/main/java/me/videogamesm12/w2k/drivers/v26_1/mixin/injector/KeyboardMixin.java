package me.videogamesm12.w2k.drivers.v26_1.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.miscellaneous.PanicKeyCombinationEvent;
import me.videogamesm12.w2k.kernel.experiment.ExperimentManager;
import me.videogamesm12.w2k.kernel.experiments.KernelExperiments;
import me.videogamesm12.w2k.toolbox.modules.QueryLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardMixin
{
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "copyCreateEntityCommand", at = @At("HEAD"))
    public void onCopyEntity(Identifier id, Vec3 pos, CompoundTag entityTag, CallbackInfo ci)
    {
        final QueryLogger module = W2K.getInstance().getModuleManager().getModule(QueryLogger.class);
        if (module.isEnabled())
        {
            module.logQueryResult(id.toString(), entityTag.toString()).whenComplete((result, error) ->
            {
                if (error != null)
                {
                    W2K.getLogger().error("Failed to dump queried entity", error);
                }
                else
                {
                    final String path = result.getSuccessful().get(0);
                    W2K.getLogger().info("Successfully dumped entity query response to " + path);

                    if (module.alert.get())
                    {
                        W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(Component.text("Entity query response logged to " + path + ".", NamedTextColor.GREEN));
                    }
                }
            });
        }
    }

    @Inject(method = "copyCreateBlockCommand", at = @At("HEAD"))
    public void onCopyBlock(BlockState state, BlockPos blockPos, CompoundTag entityTag, CallbackInfo ci)
    {
        final QueryLogger module = W2K.getInstance().getModuleManager().getModule(QueryLogger.class);
        if (module.isEnabled())
        {
            module.logQueryResult(state.typeHolder().unwrapKey().map(value -> value.identifier().toString()).orElse("air"), entityTag.toString()).whenComplete((result, error) ->
            {
                if (error != null)
                {
                    W2K.getLogger().error("Failed to dump queried block", error);
                }
                else
                {
                    final String path = result.getSuccessful().get(0);
                    W2K.getLogger().info("Successfully dumped block query response to " + path);

                    if (module.alert.get())
                    {
                        W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(Component.text("Block query response logged to " + path + ".", NamedTextColor.GREEN));
                    }
                }
            });
        }
    }

    @Inject(method = "keyPress", at = @At("HEAD"))
    public void triggerPanicKeyCombinationEvent(long handle, int action, KeyEvent event, CallbackInfo ci)
    {
        if (handle == minecraft.getWindow().handle())
        {
            // Microsoft, in their infinite "wisdom", replaced the context menu key with the stupid Copilot key because they
            //  were huffling glue trying to shove AI into absolutely everything they could. As such, some keyboards no
            //  longer have the context menu key, so we have to have a secondary combination as a backup.
            //
            // I chose CTRL + Context Menu for the primary combination and CTRL + ALT + Z for the backup combination.
            if (ExperimentManager.isExperimentEnabled(KernelExperiments.PANIC_KEY_COMBINATION) &&
                    ((event.modifiers() == GLFW.GLFW_MOD_CONTROL && event.key() == GLFW.GLFW_KEY_MENU) ||
                            (event.modifiers() == GLFW.GLFW_MOD_CONTROL + GLFW.GLFW_MOD_ALT && event.key() == GLFW.GLFW_KEY_Z)))
            {
                W2K.getEventBus().post(new PanicKeyCombinationEvent());
            }
        }
    }
}
