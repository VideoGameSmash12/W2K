package me.videogamesm12.w2k.blackbox.window.tool.helper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.tool.helper.tab.AbstractHelperTab;
import me.videogamesm12.w2k.blackbox.window.tool.helper.tab.MainHelperTab;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

public class Helper extends JFrame
{
    private final Gson gson = new Gson();

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
        pack();
    }

    public File getCustomPagesFolder()
    {
        final File folder = new File(Blackbox.getFolder(), "knowledge");
        folder.mkdirs();
        return folder;
    }

    public TreeModel getTreeModel()
    {
        final JsonElement jsonElement = gson.fromJson(new InputStreamReader(Objects.requireNonNull(Blackbox.class
                .getClassLoader().getResourceAsStream("assets/w2k-blackbox/help/index.json"))), JsonElement.class);

        return new DefaultTreeModel(getTreeNode("Help", jsonElement));
    }

    public MutableTreeNode getTreeNode(String name, JsonElement element)
    {
        final DefaultMutableTreeNode node;

        if (element.isJsonObject())
        {
            node = name.isEmpty() ? new DefaultMutableTreeNode() : new DefaultMutableTreeNode(name);
            JsonObject object = element.getAsJsonObject();
            object.entrySet().forEach(entry -> node.add(getTreeNode(entry.getKey(), entry.getValue())));
        }
        else if (element.isJsonPrimitive())
        {
            final JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (primitive.isString())
            {
                node = new PageTreeNode(name, primitive.getAsString());
            }
            else
            {
                node = new DefaultMutableTreeNode();
            }
        }
        else if (element.isJsonArray())
        {
            node = new DefaultMutableTreeNode();

            for (JsonElement member : element.getAsJsonArray())
            {
                node.add(getTreeNode("", member));
            }
        }
        else
        {
            node = new DefaultMutableTreeNode();
        }

        return node;
    }
}
