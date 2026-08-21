package me.videogamesm12.w2k.integrator.integrations.litematica.menu;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.Color4f;
import me.videogamesm12.w2k.blackbox.Blackbox;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LitematicaSettingsDialog extends JDialog
{
    public LitematicaSettingsDialog()
    {
        super(Blackbox.getInstance().getMainWindow(), "Litematica Settings");

        final JTabbedPane tabs = new JTabbedPane();
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tabs,
                GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(tabs,
                GroupLayout.Alignment.TRAILING));
        tabs.addTab("Generic", new LitematicaSettingsCategoryTab(Configs.Generic.OPTIONS));
        tabs.addTab("Colors", new LitematicaSettingsCategoryTab(Configs.Colors.OPTIONS));
        tabs.addTab("Visuals", new LitematicaSettingsCategoryTab(Configs.Visuals.OPTIONS));
        tabs.addTab("Overlays", new LitematicaSettingsCategoryTab(Configs.InfoOverlays.OPTIONS));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMaximumSize(new Dimension(480, 640));
        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));
        setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        pack();
    }

    public static class LitematicaSettingsCategoryTab extends JPanel
    {
        private final JScrollPane pane = new JScrollPane();
        private final List<IConfigBase> entries;

        public LitematicaSettingsCategoryTab(List<IConfigBase> entries)
        {
            super();
            this.entries = entries;
            pane.setViewportView(getContent());
            pane.setMaximumSize(new Dimension(480, 640));

            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(pane))
                                            ))));
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(pane))
                                            ))));

            //add(getContent());
        }

        public JPanel getContent()
        {
            final JPanel panel = new JPanel();
            GroupLayout pLayout = new GroupLayout(panel);
            pLayout.setAutoCreateGaps(true);
            pLayout.setAutoCreateContainerGaps(true);
            panel.setLayout(pLayout);
            panel.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));

            GroupLayout.Group labelsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.Group settingsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

            GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

            for (IConfigBase entry : entries)
            {
                final JLabel settingLabel = new JLabel(entry.getConfigGuiDisplayName());
                final JComponent settingComponent = getSettingComponent(entry);

                labelsHoriz = labelsHoriz.addComponent(settingLabel);
                settingsHoriz = settingsHoriz.addComponent(settingComponent);

                vertical.addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(settingLabel)
                        .addComponent(settingComponent));
            }

            pLayout.setHorizontalGroup(pLayout.createSequentialGroup()
                    .addGroup(labelsHoriz).addGroup(settingsHoriz));
            pLayout.setVerticalGroup(vertical);

            return panel;
        }

        public JComponent getSettingComponent(IConfigBase entry)
        {
            JComponent settingComponent;

            if (entry instanceof ConfigString)
            {
                final ConfigString string = (ConfigString) entry;
                settingComponent = new JTextField(string.getStringValue(), 12);
                JTextField textField = (JTextField) settingComponent;
                textField.addActionListener((e) -> string.setValueFromString(textField.getText()));
            }
            else if (entry instanceof ConfigBoolean)
            {
                final ConfigBoolean bool = (ConfigBoolean) entry;
                settingComponent = new JCheckBox("", bool.getBooleanValue());
                JCheckBox checkBox = (JCheckBox) settingComponent;
                checkBox.addActionListener((e) -> bool.setBooleanValue(checkBox.isSelected()));
            }
            else if (entry instanceof ConfigColor)
            {
                final ConfigColor color = (ConfigColor) entry;
                settingComponent = new JButton();
                JButton button = (JButton) settingComponent;

                Color convertedColor;
                try
                {
                    convertedColor = new Color(color.getColor().r, color.getColor().g, color.getColor().b, color.getColor().a);
                    button.setBackground(convertedColor);
                }
                catch (IllegalArgumentException ignored)
                {
                    convertedColor = null;
                }

                // needs to be final because lambdas are goofy i guess
                final Color finalConvertedColor = convertedColor;

                button.addActionListener(e -> {
                    Color newColor = JColorChooser.showDialog(this, "Choose a color", finalConvertedColor);
                    if (newColor != null)
                    {
                        button.setBackground(newColor);
                        color.setIntegerValue(new Color4f(newColor.getRed(), newColor.getGreen(), newColor.getBlue(),
                                newColor.getAlpha()).intValue);
                    }
                });
            }
            else if (entry instanceof ConfigDouble)
            {
                final ConfigDouble doubleEntry = (ConfigDouble) entry;
                settingComponent = new JSpinner(new SpinnerNumberModel(doubleEntry.getDoubleValue(), doubleEntry.getMinDoubleValue(), doubleEntry.getMaxDoubleValue(), 0.1));
                JSpinner spinner = (JSpinner) settingComponent;
                spinner.addChangeListener(e -> doubleEntry.setDoubleValue((double) spinner.getValue()));
            }
            else if (entry instanceof ConfigInteger)
            {
                final ConfigInteger integer = (ConfigInteger) entry;
                settingComponent = new JSpinner(new SpinnerNumberModel(integer.getIntegerValue(), integer.getMinIntegerValue(), integer.getMaxIntegerValue(), 1));
                JSpinner spinner = (JSpinner) settingComponent;
                spinner.addChangeListener(e -> integer.setIntegerValue((int) spinner.getValue()));
            }
            else
            {
                settingComponent = new JLabel("Option type not implemented");
            }

            return settingComponent;
        }
    }
}
