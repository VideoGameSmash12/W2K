package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class MainTab extends ScrollableTab
{
    private final JTextArea textArea = new JTextArea();

    public MainTab()
    {
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(true);

        setup();
    }

    @Override
    public JComponent getContentComponent()
    {
        return textArea;
    }

    @Override
    public void update()
    {
        textArea.setText(W2K.getInstance().getDriverManager().getVersionBridge().getClientDebugInformation());
    }
}
