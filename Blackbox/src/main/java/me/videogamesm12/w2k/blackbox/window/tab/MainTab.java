package me.videogamesm12.w2k.blackbox.window.tab;

import me.videogamesm12.w2k.kernel.W2K;

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
        textArea.setText(String.join("\r\n", W2K.getInstance().getVersionAbstractionLayer().getClientOverview()));
    }
}
