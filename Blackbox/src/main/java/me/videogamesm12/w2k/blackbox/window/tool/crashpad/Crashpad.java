package me.videogamesm12.w2k.blackbox.window.tool.crashpad;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Crashpad extends JFrame
{
    public Crashpad(File file)
    {
        super("Crashpad");
        setName("Crashpad");
        setMinimumSize(new Dimension(640, 360));
        setPreferredSize(new Dimension(854, 480));

        final JScrollPane scroll = new JScrollPane();
        final JTextArea textArea = new JTextArea();

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        try (BufferedReader reader = Files.newBufferedReader(file.toPath()))
        {
            textArea.read(reader, null);
        }
        catch (IOException ex)
        {
            textArea.setText("Failed to read the crash report file. It is located at " + file.getAbsolutePath() + ".");
        }
        scroll.setViewportView(textArea);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scroll,
                GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scroll,
                GroupLayout.Alignment.TRAILING));

        // Finally, we do this
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);
        pack();
    }
}
