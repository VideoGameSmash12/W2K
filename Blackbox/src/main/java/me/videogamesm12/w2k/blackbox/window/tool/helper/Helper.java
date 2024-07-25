package me.videogamesm12.w2k.blackbox.window.tool.helper;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.tool.helper.tab.AbstractHelperTab;
import me.videogamesm12.w2k.blackbox.window.tool.helper.tab.MainHelperTab;
import me.videogamesm12.w2k.kernel.W2K;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Helper extends JFrame
{
    public Helper()
    {
        super("Help");
        setName("Help");
        setMinimumSize(new Dimension(640, 360));
        setPreferredSize(new Dimension(854, 480));

        final JSplitPane split = new JSplitPane();
        split.setResizeWeight(0.25);

        final JScrollPane editorScrollPane = new JScrollPane();
        final JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorScrollPane.setViewportView(editorPane);

        final JTabbedPane tabs = new JTabbedPane();
        tabs.addChangeListener((e) ->
        {
            if (tabs.getSelectedComponent() instanceof AbstractHelperTab)
            {
                AbstractHelperTab helper = (AbstractHelperTab) tabs.getSelectedComponent();
                try
                {
                    editorPane.setPage(helper.getCurrentPage());
                }
                catch (IOException ignored)
                {
                }
            }
        });
        tabs.addTab("General", new MainHelperTab(editorPane));

        split.setLeftComponent(tabs);
        split.setRightComponent(editorScrollPane);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(split,
                GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(split,
                GroupLayout.Alignment.TRAILING));

        // Finally, we do this
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        try
        {
            InputStream iconStream = Blackbox.class.getClassLoader().getResourceAsStream("assets/w2k-blackbox/icons/default/helper.png");
            setIconImage(ImageIO.read(iconStream));
        }
        catch (IOException ex)
        {
            W2K.getLogger().error("Failed to read icon for Helper", ex);
        }

        pack();
    }
}
