package me.videogamesm12.w2k.blackbox.window.tool.helper;

import lombok.Getter;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

@Getter
public class PageTreeNode extends DefaultMutableTreeNode
{
    private final String url;
    private final boolean internal;

    public PageTreeNode(String name, File file)
    {
        super(name);
        this.url = file.getAbsolutePath();
        this.internal = false;
    }

    public PageTreeNode(String name, String url)
    {
        super(name);
        this.url = url;
        this.internal = true;
    }
}
