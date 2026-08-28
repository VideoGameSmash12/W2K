package me.videogamesm12.w2k.blackbox.window;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.blackbox.util.JComponents;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.*;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ModuleSettingsWindow<T extends WModule> extends JDialog
{
    private final T module;

    public ModuleSettingsWindow(T module)
    {
        super(Blackbox.getInstance().getMainWindow(), module.getName() + " Settings");
        this.module = module;

        GroupLayout pLayout = new GroupLayout(getContentPane());
        pLayout.setAutoCreateGaps(true);
        pLayout.setAutoCreateContainerGaps(true);
        setLayout(pLayout);

        GroupLayout.Group labelsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.Group settingsHoriz = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

        GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

        for (Map.Entry<String, WModuleSetting<?, ?>> entry : module.getSettings().entrySet())
        {
            final WModuleSetting<?, ?> setting = entry.getValue();

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

    public <S extends WModuleSetting<?, ?>> JComponent getSettingComponent(S setting)
    {
        if (setting instanceof BooleanSetting)
        {
            final BooleanSetting bool = (BooleanSetting) setting;
            return JComponents.createCheckbox(bool::get, bool::set);
        }
        else if (setting instanceof ColorSetting)
        {
            final ColorSetting color = (ColorSetting) setting;
            return JComponents.createColorPicker(this, color::get, color::set);
        }
        //else if (setting instanceof EnumSetting)
        //{
        //    final EnumSetting enumSetting = (EnumSetting) setting;
        //    return JComponents.createComboBox(enumSetting.getValues(), enumSetting::getSelected, value -> enumSetting.setSelected(value.name()));
        //}
        else if (setting instanceof IntegerSetting)
        {
            final IntegerSetting integer = (IntegerSetting) setting;
            return integer.isSpinner() ? JComponents.createSpinner(integer::get, integer::set, integer.getMinimum(), integer.getMaximum()) : JComponents.createSlider(integer::get, integer::set, integer.getMinimum(), integer.getMaximum());
        }
        else if (setting instanceof LongSetting)
        {
            final LongSetting longSetting = (LongSetting) setting;
            return JComponents.createSpinner(longSetting::get, longSetting::set, longSetting.getMinimum(), longSetting.getMaximum());
        }
        else if (setting instanceof FileSetting)
        {
            final FileSetting file = (FileSetting) setting;
            return JComponents.createFilePicker(this, file::get, file::set);
        }
        else if (setting instanceof StringSetting)
        {
            final StringSetting stringSetting = (StringSetting) setting;
            return JComponents.createTextField(stringSetting::get, stringSetting::set);
        }

        return new JLabel("Not supported currently");
    }
}
