package me.videogamesm12.w2k.integrator.integrations.meteor.menu;

import com.google.common.base.Enums;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.integrator.core.gui.PModOption;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import org.apache.commons.lang3.EnumUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        if (setting.get() instanceof String)
        {
            final Setting<String> stringSetting = (Setting<String>) setting;
            return PModOption.forString(stringSetting::get, stringSetting::set);
        }
        else if (setting.get() instanceof Boolean)
        {
            final Setting<Boolean> booleanSetting = (Setting<Boolean>) setting;
            return PModOption.forBoolean(booleanSetting::get, booleanSetting::set);
        }
        else if (setting.get() instanceof SettingColor)
        {
            final Setting<SettingColor> colorSetting = (Setting<SettingColor>) setting;
            return PModOption.forColor(this, () ->
                    new Color(colorSetting.get().r, colorSetting.get().g, colorSetting.get().b),
                    color -> colorSetting.set(new SettingColor(color)));
        }
        else if (setting.get() instanceof Integer)
        {
            final IntSetting integer = (IntSetting) setting;
            return integer.noSlider ?
                    PModOption.forIntegerSpinner(integer::get, integer::set, integer.min, integer.max) :
                    PModOption.forInteger(integer::get, integer::set, integer.sliderMin, integer.sliderMax);
        }
        else if (setting.get() instanceof Double)
        {
            final DoubleSetting doubleSetting = (DoubleSetting) setting;
            return doubleSetting.noSlider ?
                    PModOption.forDoubleSinner(doubleSetting::get, doubleSetting::set, doubleSetting.min, doubleSetting.max, 0.1) :
                    PModOption.forDouble(doubleSetting::get, doubleSetting::set, (int) Math.floor(doubleSetting.min), (int) Math.floor(doubleSetting.max));
        }
        else if (setting.get() instanceof Enum)
        {
            // This code is painted with an hour of frustratingly debugging a problem that later turned out to be caused
            //  by something completely unrelated altogether (see below). Unless you figure out a better way to
            //  implement this, DON'T TOUCH THIS IF YOU VALUE YOUR SANITY.
            EnumSetting<Enum<?>> enumSetting = (EnumSetting) setting;
            return PModOption.forEnum(new ArrayList(EnumSet.allOf(((Enum) enumSetting.get()).getDeclaringClass())), enumSetting::get, value ->
            {
                try
                {
                    enumSetting.set(value);
                }
                // Some settings call code that needs to be handled in the render thread. This doesn't work since we're
                //  on another thread, so we basically ignore it.
                // TODO: Make it run the "set" code again on the render thread if it fails before
                catch (IllegalStateException ignored)
                {
                    JOptionPane.showMessageDialog(this, "You will need to either restart your Minecraft client or set this setting manually in Meteor's configuration menu for it to take full effect.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }

        return PModOption.fallback();
    }
}
