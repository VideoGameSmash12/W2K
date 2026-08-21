package me.videogamesm12.w2k.integrator.integrations.meteor.menu;

import me.videogamesm12.w2k.blackbox.Blackbox;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import javax.swing.*;
import java.awt.*;

public class MeteorModuleSettingsDialog extends JDialog
{
    public MeteorModuleSettingsDialog(Module module)
    {
        super(Blackbox.getInstance().getMainWindow(), "Settings for " + module.title);

        BoxLayout pLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
        setLayout(pLayout);

        module.settings.groups.forEach(group ->
        {
            final JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createTitledBorder(group.name));

            GroupLayout layout = new GroupLayout(panel);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);
            panel.setLayout(layout);

            GroupLayout.Group labels = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.Group settings = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

            GroupLayout.SequentialGroup vertical = layout.createSequentialGroup();

            for (Setting<?> setting : group)
            {
                final JLabel settingLabel = new JLabel(setting.title);
                final JComponent settingComponent = getSettingComponent(setting);

                labels.addComponent(settingLabel);
                settings.addComponent(settingComponent);

                vertical.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(settingLabel)
                        .addComponent(settingComponent));
            }

            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(labels).addGroup(settings));
            layout.setVerticalGroup(vertical);

            add(panel);
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));
        setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        pack();
    }

    @SuppressWarnings("unchecked")
    public JComponent getSettingComponent(Setting<?> setting)
    {
        JComponent settingComponent;

        if (setting.get() instanceof String)
        {
            final Setting<String> stringSetting = (Setting<String>) setting;
            settingComponent = new JTextField(stringSetting.get());
            final JTextField textField = (JTextField) settingComponent;
            textField.addActionListener((e) -> stringSetting.set(textField.getText()));
        }
        else if (setting.get() instanceof Boolean)
        {
            final Setting<Boolean> booleanSetting = (Setting<Boolean>) setting;
            settingComponent = new JCheckBox("", booleanSetting.get());
            final JCheckBox checkBox = (JCheckBox) settingComponent;
            checkBox.addActionListener((e) -> booleanSetting.set(checkBox.isSelected()));
        }
        else if (setting.get() instanceof SettingColor)
        {
            final Setting<SettingColor> colorSetting = (Setting<SettingColor>) setting;
            settingComponent = new JButton();
            final JButton button = (JButton) settingComponent;

            button.setBackground(new Color(colorSetting.get().r, colorSetting.get().g, colorSetting.get().b, colorSetting.get().a));
            button.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(this, "Choose a color", button.getBackground());
                if (newColor != null)
                {
                    button.setBackground(newColor);
                    colorSetting.get().r(newColor.getRed());
                    colorSetting.get().g(newColor.getGreen());
                    colorSetting.get().b(newColor.getBlue());
                    colorSetting.get().a(newColor.getAlpha());
                }
            });
        }
        else if (setting.get() instanceof Integer)
        {
            final IntSetting integer = (IntSetting) setting;

            if (!integer.noSlider)
            {
                settingComponent = new JSlider(integer.sliderMin, integer.sliderMax, integer.get());
                final JSlider slider = (JSlider) settingComponent;
                slider.addChangeListener(e -> integer.set(slider.getValue()));
            }
            else
            {
                settingComponent = new JSpinner(new SpinnerNumberModel((int) integer.get(), integer.min, integer.max, 1));
                final JSpinner spinner = (JSpinner) settingComponent;
                spinner.addChangeListener(e -> integer.set((Integer) spinner.getValue()));
            }
        }
        else if (setting.get() instanceof Double)
        {
            final DoubleSetting doubleSetting = (DoubleSetting) setting;

            if (!doubleSetting.noSlider)
            {
                settingComponent = new JSlider((int) doubleSetting.sliderMin, (int) doubleSetting.sliderMax, (int) doubleSetting.get().doubleValue());
                final JSlider slider = (JSlider) settingComponent;
                slider.addChangeListener(e -> doubleSetting.set((double) slider.getValue()));
            }
            else
            {
                settingComponent = new JSpinner(new SpinnerNumberModel((double) doubleSetting.get(), doubleSetting.min, doubleSetting.max, 0.1d));
                final JSpinner spinner = (JSpinner) settingComponent;
                spinner.addChangeListener(e -> doubleSetting.set((Double) spinner.getValue()));
            }
        }
        else if (setting.get() instanceof Enum<?>)
        {
            final EnumSetting<Enum<?>> enumSetting = (EnumSetting<Enum<?>>) setting;
            settingComponent = new JComboBox<>(enumSetting.getSuggestions().toArray(new String[0]));
            final JComboBox<String> comboBox = (JComboBox<String>) settingComponent;
            comboBox.setSelectedItem(enumSetting.get().name());
            comboBox.addItemListener(e -> enumSetting.set(Enum.valueOf(enumSetting.get().getClass(),
                    (String) comboBox.getSelectedItem())));
        }
        else
        {
            settingComponent = new JLabel("Not supported");
        }

        return settingComponent;
    }
}
