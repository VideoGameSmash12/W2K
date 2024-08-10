package me.videogamesm12.w2k.integrator.partitions.bleachhack;

import me.videogamesm12.w2k.blackbox.Blackbox;
import org.bleachhack.setting.option.Option;

import javax.swing.*;
import java.awt.*;

public class BleachHackSettingsDialog extends JDialog
{
    public BleachHackSettingsDialog()
    {
        super(Blackbox.getInstance().getMainWindow(), "BleachHack Settings");

        GroupLayout pLayout = new GroupLayout(getContentPane());
        pLayout.setAutoCreateGaps(true);
        pLayout.setAutoCreateContainerGaps(true);
        setLayout(pLayout);

        GroupLayout.Group labelsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.Group settingsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

        for (Option<?> entry : Option.OPTIONS)
        {
            final JLabel settingLabel = new JLabel(entry.getName());
            final JComponent settingComponent = getSettingComponent(entry);

            settingLabel.setToolTipText(entry.getTooltip());
            settingComponent.setToolTipText(entry.getTooltip());

            labelsHoriz = labelsHoriz.addComponent(settingLabel);
            settingsHoriz = settingsHoriz.addComponent(settingComponent);

            vertical.addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(settingLabel)
                    .addComponent(settingComponent));
        }

        pLayout.setHorizontalGroup(pLayout.createSequentialGroup()
                .addGroup(labelsHoriz).addGroup(settingsHoriz));
        pLayout.setVerticalGroup(vertical);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));
        setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        pack();
    }

    public JComponent getSettingComponent(Option<?> setting)
    {
        JComponent settingComponent;

        switch (setting.getValue().getClass().getSimpleName().toLowerCase())
        {
            case "boolean":
            {
                final Option<Boolean> toggle = (Option<Boolean>) setting;
                settingComponent = new JCheckBox("", toggle.getValue());
                final JCheckBox checkBoxComponent = (JCheckBox) settingComponent;
                checkBoxComponent.addActionListener(e -> toggle.setValue(checkBoxComponent.isSelected()));
                break;
            }
            case "string":
            {
                final Option<String> string = (Option<String>) setting;
                settingComponent = new JTextField(string.getValue());
                final JTextField textField = (JTextField) settingComponent;
                textField.addActionListener(e -> string.setValue(textField.getText()));
                break;
            }
            default:
            {
                settingComponent = new JLabel("Not supported - " + setting.getValue().getClass().getSimpleName());
            }
        }

        return settingComponent;
    }
}
