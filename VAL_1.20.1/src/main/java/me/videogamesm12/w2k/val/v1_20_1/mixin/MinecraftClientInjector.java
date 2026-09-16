package me.videogamesm12.w2k.val.v1_20_1.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientInjector
{
    @Inject(method = "cleanUpAfterCrash", at = @At("RETURN"))
    public void onCleanUpAfterCrash(CallbackInfo ci)
    {
        W2K.getEventBus().post(new ClientCleanedUpAfterCrashEvent());
        /*if (!FabricLoader.getInstance().isModLoaded("notenoughcrashes"))
        {
            Supervisor.getInstance().shutdown();
        }
        else
        {
            Flags flags = Supervisor.getInstance().getFlags();

            if (flags.isSupposedToCrash())
            {
                flags.setSupposedToCrash(false);
            }
        }*/
    }

}
