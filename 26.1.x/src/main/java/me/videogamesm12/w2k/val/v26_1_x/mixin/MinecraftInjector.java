package me.videogamesm12.w2k.val.v26_1_x.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCleanedUpAfterCrashEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientCrashedEvent;
import me.videogamesm12.w2k.kernel.event.render.EntityGlowCheckEvent;
import me.videogamesm12.w2k.kernel.event.render.RenderCompleteEvent;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;

@Mixin(Minecraft.class)
public class MinecraftInjector
{
    @Unique
    private final RenderCompleteEvent renderCompleteEvent = new RenderCompleteEvent();
    @Unique
    private final EntityGlowCheckEvent entityGlowCheckEvent = new EntityGlowCheckEvent();

    @Inject(method = "emergencySave", at = @At("RETURN"))
    public void callClientCleanedUpAfterCrashEvent(CallbackInfo ci)
    {
        W2K.getEventBus().post(new ClientCleanedUpAfterCrashEvent());
    }

    @Inject(method = "saveReport(Ljava/io/File;Lnet/minecraft/CrashReport;)I", at = @At(value = "RETURN"))
    private static void callClientCrashedEvent(File gameDirectory, CrashReport crash, CallbackInfoReturnable<Integer> cir, @Local(name = "crashFile") Path crashFile)
    {
        // What do you mean you can't find the local Path? What???
        W2K.getEventBus().post(new ClientCrashedEvent(Minecraft.getInstance(), crash.getException(), crashFile.toFile()));
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    public void callRenderCompleteEvent(CallbackInfo ci)
    {
        W2K.getEventBus().post(renderCompleteEvent.update(System.currentTimeMillis()));
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void callEntityGlowCheck(Entity entity, CallbackInfoReturnable<Boolean> cir)
    {
        W2K.getEventBus().post(entityGlowCheckEvent.update((EntityInterface) entity));
        if (entityGlowCheckEvent.getOutcome() != null)
        {
            cir.setReturnValue(entityGlowCheckEvent.getOutcome());
        }
    }
}
