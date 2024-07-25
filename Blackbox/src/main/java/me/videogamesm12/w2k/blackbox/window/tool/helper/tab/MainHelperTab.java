package me.videogamesm12.w2k.blackbox.window.tool.helper.tab;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.tool.helper.PageTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

public class MainHelperTab extends AbstractHelperTab
{
    public MainHelperTab(JEditorPane editor)
    {
        super(editor, true);
        getJTree().setModel(getTreeModel());
    }

    @Override
    public MutableTreeNode getTree()
    {
        final JsonElement jsonElement = new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(Blackbox.class
                .getClassLoader().getResourceAsStream("assets/w2k-blackbox/help/index.json"))), JsonElement.class);

        return getTreeNode("General", jsonElement);
    }

    @Override
    public URL getDefaultPage()
    {
        return Blackbox.class.getClassLoader().getResource("assets/w2k-blackbox/help/main.html");
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
