package me.videogamesm12.w2k.blackbox.util;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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

    public static JCheckBox createCheckbox(final Supplier<Boolean> getter, final Consumer<Boolean> setter)
    {
        final JCheckBox checkBox = new JCheckBox("", getter.get());
        checkBox.addActionListener((e) -> setter.accept(checkBox.isSelected()));
        return checkBox;
    }

    public static JTextField createTextField(final Supplier<String> getter, final Consumer<String> setter)
    {
        final JTextField textField = new JTextField(getter.get());
        textField.addActionListener((e) -> setter.accept(textField.getText()));
        return textField;
    }

    public static JSlider createSlider(final Supplier<Integer> getter, final Consumer<Integer> setter, final int minimum, final int maximum)
    {
        final JSlider slider = new JSlider(minimum, maximum, getter.get());
        slider.addChangeListener(e -> setter.accept((slider.getValue())));
        return slider;
    }

    public static JSpinner createSpinner(final Supplier<Long> getter, final Consumer<Long> setter, final long minimum, final long maximum)
    {
        final JSpinner slider = new JSpinner(new SpinnerNumberModel((long) getter.get(), minimum, maximum, 1));
        slider.addChangeListener(e -> setter.accept((long) slider.getValue()));
        return slider;
    }

    public static JSpinner createSpinner(final Supplier<Integer> getter, final Consumer<Integer> setter, final int minimum, final int maximum)
    {
        final JSpinner slider = new JSpinner(new SpinnerNumberModel((int) getter.get(), minimum, maximum, 1));
        slider.addChangeListener(e -> setter.accept((int) slider.getValue()));
        return slider;
    }

    public static JButton createColorPicker(final Component parent, final Supplier<Color> getter, final Consumer<Color> setter)
    {
        final JButton button = new JButton();
        final Color value = getter.get();

        button.setBackground(value);
        button.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(parent, "Choose a color", button.getBackground());
            if (newColor != null)
            {
                button.setBackground(newColor);
                setter.accept(newColor);
            }
        });

        return button;
    }

    public static JButton createFilePicker(final Component parent, final Supplier<File> getter, final Consumer<File> setter)
    {
        final JButton button = new JButton(getter.get() != null ? getter.get().getName() : "Select File");

        button.addActionListener(e ->
        {
            final JFileChooser chooser = getter.get() != null ? new JFileChooser(getter.get()) : new JFileChooser();

            if (chooser.showDialog(parent, "Select") == JFileChooser.APPROVE_OPTION)
            {
                setter.accept(chooser.getSelectedFile());
                button.setText(chooser.getSelectedFile().getName());
            }
        });

        return button;
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
