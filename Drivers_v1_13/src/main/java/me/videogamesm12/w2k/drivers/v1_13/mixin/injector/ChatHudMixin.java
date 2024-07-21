package me.videogamesm12.w2k.drivers.v1_13.mixin.injector;

import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.event.ChatMessageEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin
{
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("RETURN"))
    public void hookForEvent(Text message, int messageId, int timestamp, boolean ignoreLimit, CallbackInfo ci)
    {
        Supervisor.getEventBus().post(new ChatMessageEvent(ComponentUtils.stringToElement(Text.Serializer.serialize(message))));
    }
}