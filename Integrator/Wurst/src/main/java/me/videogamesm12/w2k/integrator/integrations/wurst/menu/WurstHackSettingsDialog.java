package me.videogamesm12.w2k.integrator.integrations.wurst.menu;

import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.integrator.core.gui.PModOption;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
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
        final Class<? extends Setting> clazz = setting.getClass();

        if (clazz == CheckboxSetting.class || CheckboxSetting.class.isAssignableFrom(clazz))
        {
            final CheckboxSetting checkbox = (CheckboxSetting) setting;
            return PModOption.forBoolean(checkbox::isChecked, checkbox::setChecked);
        }
        else if (setting instanceof ColorSetting)
        {
            final ColorSetting color = (ColorSetting) setting;
            return PModOption.forColor(this, color::getColor, color::setColor);
        }
        else if (setting instanceof EnumSetting)
        {
            final EnumSetting<?> enumSetting = (EnumSetting<?>) setting;
            return PModOption.forEnum(enumSetting.getValues(), enumSetting::getSelected, value -> enumSetting.setSelected(value.name()));
        }
        else if (SliderSetting.class.isAssignableFrom(clazz))
        {
            final SliderSetting slider = (SliderSetting) setting;
            return PModOption.forInteger(slider::getValueI, slider::setValue, (int) slider.getMinimum(), (int) slider.getMaximum());
        }
        else if (setting instanceof FileSetting)
        {
            final FileSetting file = (FileSetting) setting;
            return PModOption.forFile(this, () -> file.getSelectedFile().toFile(), newFile -> file.setSelectedFile(newFile.getAbsolutePath()));
        }
        else if (clazz.getSimpleName().equalsIgnoreCase("TextFieldSetting"))
        {
            try
            {
                final Class<? extends Setting> textFieldClass = (Class<? extends Setting>) Class.forName("net.wurstclient.settings.TextFieldSetting");
                if (textFieldClass.isInstance(setting))
                {
                    Setting casted = textFieldClass.cast(setting);
                    Method getValueMethod = casted.getClass().getMethod("getValue");
                    Method setValueMethod = casted.getClass().getMethod("setValue", String.class);

                    return PModOption.forString(() ->
                    {
                        try
                        {
                            return (String) getValueMethod.invoke(setting);
                        }
                        catch (IllegalAccessException | InvocationTargetException ex)
                        {
                            ex.printStackTrace();
                            return "getValue returned null, please report this as a bug to W2K!";
                        }
                    }, value ->
                    {
                        try
                        {
                            setValueMethod.invoke(setting, value);
                        }
                        catch (IllegalAccessException | InvocationTargetException ex)
                        {
                            ex.printStackTrace();
                        }
                    });
                }
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }
        }

        System.out.println(setting.getName() + " - " + setting.getClass().getSimpleName());
        return PModOption.fallback();
    }
}
