package me.videogamesm12.w2k.integrator.integrations.replaymod.menu;

import com.google.common.eventbus.Subscribe;
import com.replaymod.core.ReplayMod;
import com.replaymod.recording.ReplayModRecording;
import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.integrations.replaymod.event.StateUpdateEvent;
import me.videogamesm12.w2k.integrator.integrations.replaymod.mixins.ConnectionEventHandlerAccessor;
import me.videogamesm12.w2k.integrator.integrations.replaymod.mixins.GuiRecordingControlsAccessor;

import javax.swing.*;

public class ReplayModMenu extends PModMenu<ReplayMod>
{
    private final JMenuItem status = new JMenuItem("Status: Unknown");
    private final JMenuItem startStop = new JMenuItem("Stop Recording");
    private final JMenuItem pauseUnpause = new JMenuItem("Pause Recording");

    public ReplayModMenu()
    {
        super("ReplayMod", ReplayMod.instance);
        status.setEnabled(false);
        startStop.addActionListener((e) ->
        {
            ((GuiRecordingControlsAccessor) ((ConnectionEventHandlerAccessor) ReplayModRecording.instance.getConnectionEventHandler())
                    .getGuiControls()).getButtonStartStop().onClick();
        });
        pauseUnpause.addActionListener((e) -> {
            ((GuiRecordingControlsAccessor) ((ConnectionEventHandlerAccessor) ReplayModRecording.instance.getConnectionEventHandler())
                    .getGuiControls()).getButtonPauseResume().onClick();
        });
        add(status);
        addSeparator();
        add(startStop);
        add(pauseUnpause);
        addSeparator();
        final JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener((e) -> new ReplayModSettingsDialog().setVisible(true));
        add(settings);

        Integrator.getModEventBus("integrator:replaymod").register(this);
        update(false, true, false);

        addModIconIfPresent("replaymod");
    }

    @Subscribe
    public void onStateUpdate(StateUpdateEvent event)
    {
        update(event.isPaused(), event.isStopped(), event.isAbleToStart());
    }

    private void update(boolean paused, boolean stopped, boolean canStart)
    {
        status.setText("Status: " + (stopped ? "Stopped" : paused ? "Paused" : "Recording"));
        startStop.setText((stopped ? "Start" : "Stop") + " Recording");
        startStop.setEnabled(canStart);
        pauseUnpause.setText((paused ? "Unpause" : "Pause") + " Recording");
        pauseUnpause.setEnabled(!stopped);
    }
}
