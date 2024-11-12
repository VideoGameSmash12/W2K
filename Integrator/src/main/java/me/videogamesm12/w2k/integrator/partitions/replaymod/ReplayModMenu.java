package me.videogamesm12.w2k.integrator.partitions.replaymod;

import com.google.common.eventbus.Subscribe;
import com.replaymod.core.ReplayMod;
import com.replaymod.recording.ReplayModRecording;
import com.replaymod.replay.ReplayModReplay;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.core.gui.PModMenu;
import me.videogamesm12.w2k.integrator.mixins.replaymod.ConnectionEventHandlerAccessor;
import me.videogamesm12.w2k.integrator.mixins.replaymod.GuiRecordingControlsAccessor;
import me.videogamesm12.w2k.integrator.mixins.replaymod.ReplayModAccessor;
import me.videogamesm12.w2k.integrator.partitions.replaymod.event.StateUpdateEvent;
import me.videogamesm12.w2k.kernel.W2K;
import net.fabricmc.loader.api.FabricLoader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

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
        final JMenuItem playReplayFile = new JMenuItem("Play Replay Recording...");
        playReplayFile.addActionListener((e) ->
        {
            File replayRecordingsFolder;
            // Get it directly from the horse's mouth
            try
            {
                replayRecordingsFolder = ReplayMod.instance.folders.getReplayFolder().toFile();
            }
            // Fallback
            catch (IOException ex)
            {
                replayRecordingsFolder = new File(FabricLoader.getInstance().getGameDir().toFile(), "replay_recordings");
            }

            final JFileChooser chooser = new JFileChooser(replayRecordingsFolder);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("Replay Mod Recording", "mcpr"));
            chooser.setDialogTitle("Choose replay recording file to play back");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                // If we're connected to a server, disconnect
                try
                {
                    W2K.getInstance().getDriverManager().getVersionBridge().disconnect();
                }
                catch (IllegalStateException ignored)
                {
                }

                ((ReplayModAccessor) ReplayMod.instance).getScheduler().runLater(() ->
                {
                    try
                    {
                        ReplayModReplay.instance.startReplay(chooser.getSelectedFile());
                    }
                    catch (IOException ex)
                    {
                        SwingUtilities.invokeLater(() ->
                        {
                            W2K.getLogger().error("Failed to read replay recording file", ex);
                            JOptionPane.showMessageDialog(Blackbox.getInstance().getMainWindow(), ex.getLocalizedMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                });
            }
        });
        add(playReplayFile);

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
