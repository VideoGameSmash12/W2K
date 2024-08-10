package me.videogamesm12.w2k.integrator.partitions.bleachhack;

import me.videogamesm12.w2k.blackbox.Blackbox;
import org.bleachhack.module.Module;
import org.bleachhack.setting.module.*;

import javax.swing.*;
import java.awt.*;

public class BleachHackModuleSettingsDialog extends JDialog
{
    public BleachHackModuleSettingsDialog(Module module)
    {
        super(Blackbox.getInstance().getMainWindow(), "Settings for " + module.getName());

        GroupLayout pLayout = new GroupLayout(getContentPane());
        pLayout.setAutoCreateGaps(true);
        pLayout.setAutoCreateContainerGaps(true);
        setLayout(pLayout);

        GroupLayout.Group labelsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.Group settingsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

        for (ModuleSetting<?> entry : module.getSettings())
        {
            // Key bindings are not supported at this moment in time as AWT/Swing, LWJGL, and GLFW all have different
            //  internal codes for keys and are thus not compatible. I don't want to have to juggle these and risk
            //  causing compatibility issues. Screw that.
            if (entry instanceof SettingKey)
            {
                continue;
            }

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

    public JComponent getSettingComponent(ModuleSetting<?> setting)
    {
        JComponent settingComponent;

        switch (setting.getClass().getSimpleName())
        {
            case "SettingMode":
            {
                final SettingMode mode = setting.asMode();
                settingComponent = new JComboBox<>(mode.modes);
                final JComboBox<String> comboBox = (JComboBox<String>) settingComponent;
                comboBox.setSelectedItem(mode.modes[mode.getMode()]);
                comboBox.addItemListener(e -> mode.setValue(comboBox.getSelectedIndex()));
                break;
            }
            case "SettingRotate":
            case "SettingToggle":
            {
                final SettingToggle toggle = setting.asToggle();
                settingComponent = new JCheckBox("", toggle.getState());
                final JCheckBox checkBoxComponent = (JCheckBox) settingComponent;
                checkBoxComponent.addActionListener(e -> toggle.setValue(checkBoxComponent.isSelected()));
                break;
            }
            case "SettingSlider":
            {
                final SettingSlider slider = setting.asSlider();
                settingComponent = new JSlider((int) slider.min, (int) slider.max, slider.getValueInt());
                final JSlider sliderComponent = (JSlider) settingComponent;
                sliderComponent.addChangeListener(e -> slider.setValue((double) sliderComponent.getValue()));
                break;
            }
            case "SettingColor":
            {
                final SettingColor color = setting.asColor();
                settingComponent = new JButton();
                final JButton button = (JButton) settingComponent;

                button.setBackground(new Color(color.getRGB()));
                button.addActionListener(e -> {
                    Color newColor = JColorChooser.showDialog(this, "Choose a color", new Color(color.getRGB()));
                    if (newColor != null)
                    {
                        button.setBackground(newColor);
                        color.setValue(Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), null));
                    }
                });
                break;
            }
            case "SettingButton":
            {
                final SettingButton settingButton = setting.asButton();
                settingComponent = new JButton("Run");
                final JButton button = (JButton) settingComponent;
                button.addActionListener((e) -> settingButton.action.run());
                break;
            }
            default:
            {
                settingComponent = new JLabel("Not supported - " + setting.getClass().getSimpleName());
            }
        }

        return settingComponent;
    }
}
