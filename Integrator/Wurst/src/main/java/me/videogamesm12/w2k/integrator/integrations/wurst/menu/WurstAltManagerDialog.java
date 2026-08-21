package me.videogamesm12.w2k.integrator.integrations.wurst.menu;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.integrator.Integrator;
import me.videogamesm12.w2k.integrator.integrations.wurst.event.AltListChangedEvent;
import net.wurstclient.WurstClient;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.altmanager.CrackedAlt;
import net.wurstclient.altmanager.LoginException;
import net.wurstclient.altmanager.MojangAlt;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

public class WurstAltManagerDialog extends JDialog
{
    private final JTable table;

    public WurstAltManagerDialog()
    {
        super(Blackbox.getInstance().getMainWindow(), "Alt Manager");

        table = new JTable(new AltTableModel());
        table.setRowSelectionAllowed(true);
        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent event)
            {
                if (SwingUtilities.isRightMouseButton(event) && event.getComponent() instanceof JTable)
                {
                    final JPopupMenu menu = new JPopupMenu();

                    if (table.getSelectedRow() != 1)
                    {
                        Alt alt = WurstClient.INSTANCE.getAltManager().getList().get(table.getSelectedRow());
                        final JMenuItem login = new JMenuItem("Login as " + alt.getName());
                        login.addActionListener(e ->
                        {
                            try
                            {
                                alt.login();
                                JOptionPane.showMessageDialog(WurstAltManagerDialog.this,
                                        "Successfully logged in as " + alt.getDisplayName() + ".", "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                            catch (LoginException ex)
                            {
                                JOptionPane.showMessageDialog(WurstAltManagerDialog.this,
                                        "Failed to log in. Wrong password?", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        menu.add(login);
                        final JMenuItem edit = new JMenuItem("Edit this alt");
                        edit.addActionListener(e ->
                        {
                            JOptionPane.showMessageDialog(WurstAltManagerDialog.this, "Placeholder.");
                        });
                        menu.add(edit);
                        final JMenuItem remove = new JMenuItem("Remove this alt");
                        remove.addActionListener(e ->
                        {
                            if (table.getSelectedRow() == -1)
                                return;

                            if (JOptionPane.showConfirmDialog(WurstAltManagerDialog.this,
                                    "Are you sure you want to delete this alt?\n" + alt.getDisplayName(),
                                    "Just to make sure...", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                            {
                                WurstClient.INSTANCE.getAltManager().remove(table.getSelectedRow());
                            }
                        });
                        menu.add(remove);
                        menu.addSeparator();
                    }
                    final JMenuItem add = new JMenuItem("Add new alt");
                    add.addActionListener(e ->
                    {
                        JOptionPane.showMessageDialog(WurstAltManagerDialog.this, "Placeholder.");
                    });
                    menu.add(add);

                    menu.show(WurstAltManagerDialog.this, event.getX(), event.getY());
                }
            }
        });

        final JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(table);

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setAutoCreateContainerGaps(true);
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scroll,
                GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(scroll,
                GroupLayout.Alignment.TRAILING));

        Integrator.getModEventBus("integrator:wurst").register(this);

        setPreferredSize(new Dimension(480, 320));
        pack();
    }

    @Subscribe
    public void onAltChanged(AltListChangedEvent event)
    {
        W2K.getLogger().warn("DEBUG! TABLE GRABBED");
        ((AltTableModel) table.getModel()).fireTableDataChanged();
    }

    public static class AltEditorDialog extends JDialog
    {
        private final JLabel nameLabel = new JLabel("Username (or Email):");
        private final JLabel passwordLabel = new JLabel("Password:");
        //--
        private final JTextField nameField = new JTextField();
        private final JPasswordField passwordField = new JPasswordField();
        //--
        private final JButton ok = new JButton("OK");
        private final JButton cancel = new JButton("Cancel");

        public AltEditorDialog(Alt alt, boolean newAlt)
        {
            nameField.setText(alt.getName());
            passwordField.setText(alt instanceof MojangAlt ? ((MojangAlt) alt).getPassword() : "");

            ok.setText(newAlt ? "Create" : "OK");
        }
    }

    public static class AltTableModel extends AbstractTableModel
    {
        final List<String> columnNames = Arrays.asList("Active", "Fav", "Username", "Type");

        @Override
        public String getColumnName(int column)
        {
            return columnNames.get(column);
        }

        // Name, Type, Favorite
        @Override
        public int getRowCount()
        {
            return WurstClient.INSTANCE.getAltManager().getList().size();
        }

        @Override
        public int getColumnCount()
        {
            return columnNames.size();
        }

        @Override
        public Object getValueAt(int row, int column)
        {
            final Alt alt = WurstClient.INSTANCE.getAltManager().getList().get(row);
            switch (column)
            {
                case 0:
                {
                    return W2K.getInstance().getDriverManager().getVersionBridge().getCurrentUsername()
                            .equalsIgnoreCase(alt.getName()) ? "*" : "";
                }
                case 1:
                {
                    return alt.isFavorite() ? "⭐" : "";
                }
                case 2:
                {
                    return alt.getDisplayName();
                }
                case 3:
                {
                    return alt instanceof CrackedAlt ? "Cracked" : "Premium";
                }
                default:
                {
                    return null;
                }
            }
        }
    }
}
