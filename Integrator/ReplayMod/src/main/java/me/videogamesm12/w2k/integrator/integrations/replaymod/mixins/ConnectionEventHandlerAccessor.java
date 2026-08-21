package me.videogamesm12.w2k.integrator.integrations.replaymod.mixins;

import com.replaymod.recording.gui.GuiRecordingControls;
import com.replaymod.recording.handler.ConnectionEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConnectionEventHandler.class)
public interface ConnectionEventHandlerAccessor
{
    @Accessor
    public GuiRecordingControls getGuiControls();
}
