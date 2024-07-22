package me.videogamesm12.w2k.drivers.v1_14.mixin.injector;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.command.WCommand;
import net.kyori.adventure.text.Component;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(String string, CallbackInfo ci)
    {
        if (string.startsWith("/"))
        {
            final String[] commandSet = string.split(" ");
            final String cmd = commandSet[0].replace("/", "");

            if (W2K.getInstance().getCommandManager().getCommandNames().contains(cmd))
            {
                final WCommand command = W2K.getInstance().getCommandManager().getCommand(cmd);

                try
                {
                    if (!command.executeCommand(cmd, ArrayUtils.remove(commandSet, 0)))
                    {
                        command.msg(Component.translatable("w2k.command.command_usage",
                                Component.text(command.getUsage().replace("<command>", cmd))));
                    }
                }
                catch (Throwable ex)
                {
                    command.msg(Component.translatable("w2k.command.command_error", Component.text(ex.getLocalizedMessage())));
                }

                ci.cancel();
            }
        }
    }
}
