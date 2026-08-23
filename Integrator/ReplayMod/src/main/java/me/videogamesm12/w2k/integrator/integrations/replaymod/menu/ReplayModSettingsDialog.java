package me.videogamesm12.w2k.integrator.integrations.replaymod.menu;

import com.replaymod.core.ReplayMod;
import com.replaymod.core.SettingsRegistry;
import com.replaymod.replaystudio.util.I18n;
import me.videogamesm12.w2k.blackbox.Blackbox;
import me.videogamesm12.w2k.integrator.core.gui.PModOption;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReplayModSettingsDialog extends JDialog
{
    public ReplayModSettingsDialog()
    {
        super(Blackbox.getInstance().getMainWindow(), "ReplayMod Settings");

        BoxLayout pLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
        setLayout(pLayout);

        add(new ReplayModSettingsSection("General", ReplayMod.instance.getSettingsRegistry().getSettings()
                .stream().filter(entry -> entry.getDisplayString() != null).collect(Collectors.toList())));

        add(new ReplayModSettingsSection("Secret", ReplayMod.instance.getSettingsRegistry().getSettings()
                .stream().filter(entry -> entry.getDisplayString() == null).collect(Collectors.toList())));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(getContentPane().getWidth(), getContentPane().getHeight()));
        setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        pack();
    }

    public static <T> JComponent getSettingComponent(SettingsRegistry.SettingKey<T> key)
    {
        JComponent settingComponent;
        SettingsRegistry registry = ReplayMod.instance.getSettingsRegistry();

        if (SettingsRegistry.MultipleChoiceSettingKey.class.isAssignableFrom(key.getClass()))
        {
            final SettingsRegistry.MultipleChoiceSettingKey<T> choiceKey = (SettingsRegistry.MultipleChoiceSettingKey<T>) key;

            if (choiceKey.getDefault() instanceof String)
            {
                final SettingsRegistry.MultipleChoiceSettingKey<String> multiString = (SettingsRegistry.MultipleChoiceSettingKey<String>) choiceKey;
                return PModOption.forPracticalEnum(multiString.getChoices(),
                        () -> registry.get(multiString),
                        value -> registry.set(multiString, value));
            }
            else
            {
                settingComponent = new JLabel("Multiple choice - not implemented");
            }
        }
        else
        {
            if (key.getDefault() instanceof Integer)
            {
                final SettingsRegistry.SettingKey<Integer> integerKey = (SettingsRegistry.SettingKey<Integer>) key;
                return PModOption.forIntegerSpinner(() -> registry.get(integerKey),
                        value -> registry.set(integerKey, value),
                        0,
                        Integer.MAX_VALUE);
            }
            else if (key.getDefault() instanceof String)
            {
                final SettingsRegistry.SettingKey<String> stringKey = (SettingsRegistry.SettingKey<String>) key;
                return PModOption.forString(() -> registry.get(stringKey),
                        value -> registry.set(stringKey, value));
            }
            else if (key.getDefault() instanceof Boolean)
            {
                final SettingsRegistry.SettingKey<Boolean> boolKey = (SettingsRegistry.SettingKey<Boolean>) key;
                return PModOption.forBoolean(() -> registry.get(boolKey),
                        value -> registry.set(boolKey, value));
            }
            else
            {
                settingComponent = new JLabel("Not implemented - " + key.getClass().getName() + " - " + key.getDefault().getClass().getName());
            }
        }

        settingComponent.setName(key.getKey());

        return settingComponent;
    }

    public static class ReplayModSettingsSection extends JPanel
    {
        public ReplayModSettingsSection(String title, List<SettingsRegistry.SettingKey<?>> entries)
        {
            super();
            setBorder(BorderFactory.createTitledBorder(title));

            GroupLayout pLayout = new GroupLayout(this);
            pLayout.setAutoCreateGaps(true);
            pLayout.setAutoCreateContainerGaps(true);
            setLayout(pLayout);

            GroupLayout.Group labels = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
            GroupLayout.Group settings = pLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

            GroupLayout.SequentialGroup vertical = pLayout.createSequentialGroup();

            for (SettingsRegistry.SettingKey<?> entry : entries)
            {
                final JLabel settingLabel = new JLabel(I18n.format("replaymod.gui.settings." + entry.getKey().toLowerCase()));
                final JComponent settingComponent = getSettingComponent(entry);

                labels.addComponent(settingLabel);
                settings.addComponent(settingComponent);

                vertical.addGroup(pLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(settingLabel)
                        .addComponent(settingComponent));
            }

            pLayout.setHorizontalGroup(pLayout.createSequentialGroup()
                    .addGroup(labels).addGroup(settings));
            pLayout.setVerticalGroup(vertical);
        }
    }
}
