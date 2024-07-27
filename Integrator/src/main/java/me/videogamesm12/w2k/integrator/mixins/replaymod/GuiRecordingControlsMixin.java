package me.videogamesm12.w2k.integrator.mixins.replaymod;

import com.replaymod.recording.gui.GuiRecordingControls;
import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.partitions.replaymod.event.StateUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRecordingControls.class)
public class GuiRecordingControlsMixin
{
    @Shadow private boolean stopped;

    @Shadow private boolean paused;

    @Inject(method = "updateState", at = @At("TAIL"), remap = false)
    public void onUpdateState(CallbackInfo ci)
    {
        Integrator.getModEventBus("integrator:replaymod").post(new StateUpdateEvent(this.stopped, this.paused, true));
    }
}
