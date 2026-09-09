package me.videogamesm12.w2k.blackbox2.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ConnectingWindow extends JDialog
{
    private final JProgressBar progressBar;

    public ConnectingWindow()
    {
        setTitle("Connecting to 0.0.0.0:32767...");

        GroupLayout pLayout = new GroupLayout(getContentPane());
        pLayout.setAutoCreateGaps(true);
        pLayout.setAutoCreateContainerGaps(true);
        setLayout(pLayout);

        GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

        final JLabel label = new JLabel("Connecting to 0.0.0.0:32767...");

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(75);
        progressBar.setString("Connecting...");

        vertical.addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(label)/*.addComponent(progressBar)*/);
        vertical.addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(progressBar));

        /*pLayout.setHorizontalGroup(pLayout.createSequentialGroup()
                .addGroup(horizontal)
                .addGroup(horizontal2));*/

        pLayout.setHorizontalGroup(pLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(label).addComponent(progressBar));
        pLayout.setVerticalGroup(vertical);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));
        setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        pack();
    }
}
