package me.videogamesm12.w2k.drivers.v1_20_6.mixin.injector;

import me.videogamesm12.w2k.drivers.v1_20_6.required.W1206VersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.event.ChatMessageEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin
{
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("RETURN"))
    public void hookForEvent(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci)
    {
        Supervisor.getEventBus().post(new ChatMessageEvent(ComponentUtils.stringToElement(
                Text.Serialization.toJsonString(message, W1206VersionBridgeDriver.getWrapperLookup()))));
    }
}
