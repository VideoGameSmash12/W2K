package me.videogamesm12.w2k.val.v26_2.mixin;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.event.hud.ChatMessageAddedEvent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentInjector
{
    @Inject(method = "addMessage", at = @At("RETURN"))
    public void callChatMessageAddedEvent(Component contents, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag, CallbackInfo ci)
    {
        W2K.getEventBus().post(new ChatMessageAddedEvent(W2K.getInstance().getVersionAbstractionLayer().text().nativeToAdventure(contents)));
    }
}
