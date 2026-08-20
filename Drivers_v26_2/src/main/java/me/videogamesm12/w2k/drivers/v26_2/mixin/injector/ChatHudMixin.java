package me.videogamesm12.w2k.drivers.v26_2.mixin.injector;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.supervisor.Supervisor;
import me.videogamesm12.w2k.supervisor.api.event.ChatMessageEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatHudMixin
{
    @Inject(method = "addMessage", at = @At("RETURN"))
    public void hookForEvent(net.minecraft.network.chat.Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci)
    {
        // Encode the Text as JSON using internal shenanigans. If it fails, then we try stripping it of any additional
        //  formatting and trying that.
        DataResult<JsonElement> regular = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, contents);
        regular.ifSuccess(element -> Supervisor.getEventBus().post(new ChatMessageEvent(element)));
        regular.ifError(error -> Supervisor.getEventBus().post(new ChatMessageEvent(
                ComponentUtils.serializeComponent(Component.text(contents.getString()).appendSpace().append(
                        Component.text("(!)"))))));
    }
}
