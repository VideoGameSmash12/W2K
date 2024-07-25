package me.videogamesm12.w2k.blackbox.window.tool.helper.tab;

import lombok.Getter;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.window.tool.helper.PageTreeNode;
import me.videogamesm12.w2k.kernel.W2K;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Getter
public abstract class AbstractHelperTab extends JScrollPane
{
    private final JEditorPane webViewer;
    private final JTree jTree;
    //
    @Getter
    private URL currentPage;

    public AbstractHelperTab(JEditorPane editor, boolean def)
    {
        super();
        //--
        this.webViewer = editor;
        this.jTree = new JTree();
        jTree.addTreeSelectionListener(treeSelectionEvent ->
        {
            if (jTree.getLastSelectedPathComponent() instanceof PageTreeNode)
            {
                final PageTreeNode node = (PageTreeNode) jTree.getLastSelectedPathComponent();

                try
                {
                    final URL url = node.isInternal() ? Blackbox.class.getClassLoader().getResource(node.getUrl()) : new URL(node.getUrl());
                    setToPage(url);
                }
                catch (MalformedURLException ex)
                {
                    W2K.getLogger().error("Failed to navigate to page {}", node.getUrl(), ex);
                }

            }
        });
        //--
        this.currentPage = getDefaultPage();
        setViewportView(jTree);
        //--
        if (def)
        {
            setToPage(currentPage);
        }
    }

    public void setToPage(URL url)
    {
        if (url == null)
        {
            return;
        }

        try
        {
            currentPage = url;
            webViewer.setPage(url);
        }
        catch (IOException ex)
        {
            W2K.getLogger().error("Failed to load page", ex);
        }
    }

    public abstract MutableTreeNode getTree();

    public DefaultTreeModel getTreeModel()
    {
        return new DefaultTreeModel(getTree());
    }

    public abstract URL getDefaultPage();
}
