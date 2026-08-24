package me.videogamesm12.w2k.blackbox.util;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JComponents
{
    public static JCheckBoxMenuItem createCheckboxMenuItem(final String label, final String tooltip,
                                                           final Supplier<Boolean> getter, final Consumer<Boolean> setter)
    {
        final JCheckBoxMenuItem checkbox = new JCheckBoxMenuItem(label, getter.get());
        if (tooltip != null)
            checkbox.setToolTipText(tooltip);
        checkbox.addActionListener(e -> setter.accept(checkbox.isSelected()));
        return checkbox;
    }

    public static JMenuItem createMenuItem(final String label, final String tooltip, final Runnable action)
    {
        final JMenuItem item = new JMenuItem(label);
        if (tooltip != null)
            item.setToolTipText(tooltip);
        item.addActionListener(e -> action.run());
        return item;
    }
}
