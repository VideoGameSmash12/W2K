package me.videogamesm12.w2k.drivers.v1_21.mixin.injector;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.drivers.v1_21.required.W121VersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.event.ChatMessageEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
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
        // Encode the Text as JSON using internal shenanigans. If it fails, then we try stripping it of any additional
        //  formatting and trying that.
        DataResult<JsonElement> regular = TextCodecs.CODEC.encodeStart(W121VersionBridgeDriver.getWrapperLookup()
                .getOps(JsonOps.INSTANCE), message);
        regular.ifSuccess(element -> Supervisor.getEventBus().post(new ChatMessageEvent(element)));
        regular.ifError(error -> Supervisor.getEventBus().post(new ChatMessageEvent(
                ComponentUtils.serializeComponent(Component.text(message.getString()).appendSpace().append(
                        Component.text("(!)"))))));
    }
}
