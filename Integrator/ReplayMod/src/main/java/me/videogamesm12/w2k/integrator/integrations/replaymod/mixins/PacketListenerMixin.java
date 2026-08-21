package me.videogamesm12.w2k.integrator.integrations.replaymod.mixins;

import com.replaymod.recording.packet.PacketListener;
import io.netty.channel.ChannelHandlerContext;
import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.integrations.replaymod.event.StateUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketListener.class)
public class PacketListenerMixin
{
    @Inject(method = "channelInactive", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;start()V", shift = At.Shift.AFTER), remap = false)
    public void onChannelInactive(ChannelHandlerContext ctx, CallbackInfo ci)
    {
        Integrator.getModEventBus("integrator:replaymod").post(new StateUpdateEvent(true, false, false));
    }
}
