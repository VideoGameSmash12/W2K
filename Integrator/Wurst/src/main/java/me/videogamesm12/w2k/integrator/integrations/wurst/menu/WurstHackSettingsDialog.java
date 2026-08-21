package me.videogamesm12.w2k.integrator.integrations.wurst.menu;

import me.videogamesm12.w2k.blackbox.Blackbox;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.Map;

public class WurstHackSettingsDialog extends JDialog
{
    public WurstHackSettingsDialog(Hack hack)
    {
        super(Blackbox.getInstance().getMainWindow(), "Settings for " + hack.getName());

        GroupLayout pLayout = new GroupLayout(getContentPane());
        pLayout.setAutoCreateGaps(true);
        pLayout.setAutoCreateContainerGaps(true);
        setLayout(pLayout);

        GroupLayout.Group labelsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.Group settingsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

        for (Map.Entry<String, Setting> entry : hack.getSettings().entrySet())
        {
            Setting setting = entry.getValue();

            final JLabel settingLabel = new JLabel(setting.getName());
            final JComponent settingComponent = getSettingComponent(setting);

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

    public JComponent getSettingComponent(Setting setting)
    {
        JComponent settingComponent;

        switch (setting.getClass().getSimpleName())
        {
            case "FilterArmorStandsSetting":
            case "FilterNamedSetting":
            case "FilterInvisibleSetting":
            case "FilterGolemsSetting":
            case "FilterTradersSetting":
            case "FilterAnimalsSetting":
            case "FilterMonstersSetting":
            case "FilterPlayersSetting":
            case "CheckboxSetting":
            {
                final CheckboxSetting chkBox = (CheckboxSetting) setting;
                settingComponent = new JCheckBox("", chkBox.isChecked());
                final JCheckBox checkBoxComponent = (JCheckBox) settingComponent;

                checkBoxComponent.addActionListener(e -> chkBox.setChecked(checkBoxComponent.isSelected()));
                break;
            }
            case "SliderSetting":
            {
                final SliderSetting slider = (SliderSetting) setting;
                settingComponent = new JSlider((int) slider.getMinimum(), (int) slider.getMaximum(), slider.getValueI());
                final JSlider sliderComponent = (JSlider) settingComponent;

                sliderComponent.addChangeListener(e -> slider.setValue((sliderComponent.getValue())));
                break;
            }
            case "ColorSetting":
            {
                final ColorSetting color = (ColorSetting) setting;
                settingComponent = new JButton();
                final JButton button = (JButton) settingComponent;

                button.setBackground(color.getColor());
                button.addActionListener(e -> {
                    Color newColor = JColorChooser.showDialog(this, "Choose a color", color.getColor());
                    if (newColor != null)
                    {
                        button.setBackground(newColor);
                        color.setColor(newColor);
                    }
                });
                break;
            }
            case "EnumSetting":
            {
                final EnumSetting enumSetting = (EnumSetting) setting;
                settingComponent = new JComboBox<Enum<?>>(enumSetting.getValues());
                final JComboBox comboBox = (JComboBox) settingComponent;

                comboBox.setSelectedItem(enumSetting.getSelected());
                comboBox.addItemListener(e -> enumSetting.setSelected((Enum<?>) e.getItem()));
                break;
            }
            case "FileSetting":
            {
                final FileSetting file = (FileSetting) setting;
                settingComponent = new JButton("Select File");
                final JButton button = (JButton) settingComponent;

                button.addActionListener(e ->
                {
                    final JFileChooser chooser = file.getSelectedFile() != null ?
                            new JFileChooser(file.getSelectedFile().toFile()) : new JFileChooser();

                    if (chooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION)
                    {
                        file.setSelectedFile(chooser.getSelectedFile().getAbsolutePath());
                    }
                });
                break;
            }
            case "TextFieldSetting":
            {
                try
                {
                    Class<? extends Setting> textFieldClass = (Class<? extends Setting>) Class.forName("net.wurstclient.settings.TextFieldSetting");
                    if (textFieldClass.isInstance(setting))
                    {
                        Setting casted = textFieldClass.cast(setting);
                        Method getValueMethod = casted.getClass().getMethod("getValue");
                        Method setValueMethod = casted.getClass().getMethod("setValue", String.class);

                        String value = (String) getValueMethod.invoke(casted);
                        settingComponent = new JTextField(value);
                        final JTextField textField = (JTextField) settingComponent;

                        textField.addActionListener(e ->
                        {
                            try
                            {
                                setValueMethod.invoke(casted, textField.getText());
                            }
                            catch (Throwable ex)
                            {
                                ex.printStackTrace();
                            }
                        });
                    }
                    else
                    {
                        settingComponent = null;
                    }
                }
                catch (Throwable ex)
                {
                    settingComponent = new JLabel("Not supported");
                    ex.printStackTrace();
                }
                break;
            }
            default:
            {
                settingComponent = new JLabel("Not supported");
                System.out.println(setting.getName() + " - " + setting.getClass().getSimpleName());
            }
        }

        return settingComponent;
    }
}
