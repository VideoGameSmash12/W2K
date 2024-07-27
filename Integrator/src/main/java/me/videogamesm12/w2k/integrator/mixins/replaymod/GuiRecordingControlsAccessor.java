package me.videogamesm12.w2k.integrator.mixins.replaymod;

import com.replaymod.lib.de.johni0702.minecraft.gui.element.GuiButton;
import com.replaymod.recording.gui.GuiRecordingControls;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiRecordingControls.class)
public interface GuiRecordingControlsAccessor
{
    @Accessor
    GuiButton getButtonPauseResume();

    @Accessor
    GuiButton getButtonStartStop();
}
