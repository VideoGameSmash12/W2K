package me.videogamesm12.w2k.integrator.partitions.meteor;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.integrator.mixins.meteor_client.FontFamilyAccessor;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class MeteorModuleSettingsDialog extends JDialog
{
    private static final Map<FontFace, Font> fontCache = new HashMap<>();

    // Cache Meteor's fonts so that we don't have to constantly recreate new Font instances every time
    static
    {

    }

    public MeteorModuleSettingsDialog(Module module)
    {
        super(Blackbox.getInstance().getMainWindow(), "Settings for " + module.title);

        BoxLayout pLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
        setLayout(pLayout);

        // Getting Fonts for every FontFace is a very intensive task that tanks performance and resources. To at least
        //  make it bearable, we cache fonts ahead of time so that we don't have to later on. If we somehow encounter a
        //  font that isn't cached, we'll just generate it on runtime and then cache it.
        if (fontCache.isEmpty())
        {
            Fonts.FONT_FAMILIES.forEach(family -> ((FontFamilyAccessor) family).getFonts().forEach(face ->
            {
                try
                {
                    fontCache.put(face, Font.createFont(Font.TRUETYPE_FONT, face.toStream()).deriveFont(12.0F));
                }
                catch (IOException | FontFormatException ignored)
                {
                }
            }));
        }

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
                final JComponent settingComponent = getSettingComponent(this, setting);

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
    public static JComponent getSettingComponent(Component parent, Setting<?> setting)
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
            button.setText("\t");
            button.setBackground(new Color(colorSetting.get().r, colorSetting.get().g, colorSetting.get().b, colorSetting.get().a));
            button.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(parent, "Choose a color", button.getBackground());
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
        else if (setting.get() instanceof FontFace)
        {
            final List<FontFace> fonts = new ArrayList<>();
            Fonts.FONT_FAMILIES.forEach(family -> fonts.addAll(((FontFamilyAccessor) family).getFonts()));

            final FontFaceSetting fontFaceSetting = (FontFaceSetting) setting;
            settingComponent = new JComboBox<>(fonts.toArray());
            final JComboBox<FontFace> comboBox = (JComboBox<FontFace>) settingComponent;
            comboBox.setSelectedItem(fontFaceSetting.get());
            comboBox.addItemListener(e ->
            {
                fontFaceSetting.set((FontFace) comboBox.getSelectedItem());
                comboBox.setFont(fontCache.get(fontFaceSetting.get()));
            });
            comboBox.setRenderer(new FontCellRenderer());
            comboBox.setFont(fontCache.get(fontFaceSetting.get()));
        }
        else
        {
            settingComponent = new JLabel("Not supported");
        }

        return settingComponent;
    }

    private static class FontCellRenderer extends DefaultListCellRenderer
    {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            if (value instanceof FontFace)
            {
                final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                final FontFace fontFace = (FontFace) value;

                try
                {
                    if (!fontCache.containsKey(fontFace))
                    {
                        fontCache.put(fontFace, Font.createFont(Font.TRUETYPE_FONT, fontFace.toStream()).deriveFont(12.0F));
                    }

                    Font font = fontCache.get((FontFace) value);

                    label.setFont(font);
                    label.setText(font.getFontName());
                }
                catch (IOException | FontFormatException ex)
                {
                    // Fallback to the one being used before everything went to shit
                    fontCache.put(fontFace, label.getFont());
                }

                return label;
            }

            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
