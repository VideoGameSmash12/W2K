package me.videogamesm12.w2k.blackbox.window.tool.console;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.supervisor.Supervisor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class AbstractTab<T extends JComponent> extends JPanel
{
    public final T outputBox;

    public AbstractTab(final T outputBox)
    {
        final JScrollPane scroll = new JScrollPane();
        this.outputBox = outputBox;
        scroll.setViewportView(outputBox);

        final JLabel besidesField = new JLabel("Input:");
        final JTextField field = new JTextField();
        final JButton sendButton = new JButton("Send");

        field.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        send(field.getText());
                    }
                    catch (Throwable ex)
                    {
                        showMessage(ex.getMessage());
                    }
                }
            }
        });
        sendButton.addActionListener((e) ->
        {
            try
            {
                send(field.getText());
            }
            catch (Throwable ex)
            {
                showMessage(ex.getMessage());
            }
        });

        GroupLayout pLayout = new GroupLayout(this);
        setLayout(pLayout);
        //--
        pLayout.setHorizontalGroup(
                pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(pLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(scroll)
                                        .addGroup(pLayout.createSequentialGroup()
                                                .addComponent(besidesField)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(field, GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sendButton)))
                                .addContainerGap()));
        pLayout.setVerticalGroup(
                pLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, pLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                                .addContainerGap()
                                .addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(besidesField)
                                        .addComponent(field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(sendButton))
                                .addContainerGap())
        );
    }

    public void showMessage(JsonElement text)
    {
        if (shouldDisplay(text))
        {
            showMessage(W2K.getInstance().getDriverManager().getVersionBridge().textToString(text));
        }
    }

    public abstract void showMessage(String text);

    public abstract void clear();

    protected void send(String messageOrCommand)
    {
        if (messageOrCommand.startsWith("/"))
        {
            Supervisor.getInstance().runCommand(messageOrCommand.substring(1));
        }
        else
        {
            Supervisor.getInstance().chatMessage(messageOrCommand);
        }
    }

    /**
     * Filters the message to display.
     * @param message   Text
     * @return          True if the message should go through.
     */
    public abstract boolean shouldDisplay(JsonElement message);

    /**
     * Returns the intended tab name.
     * @return String
     */
    public abstract String name();
}
