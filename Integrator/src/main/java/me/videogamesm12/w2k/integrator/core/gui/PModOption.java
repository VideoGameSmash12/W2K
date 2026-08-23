package me.videogamesm12.w2k.integrator.core.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PModOption
{
    public static JTextField forString(final Supplier<String> getter, final Consumer<String> setter)
    {
        final JTextField textField = new JTextField(getter.get());
        textField.addActionListener((e) -> setter.accept(textField.getText()));
        return textField;
    }

    public static JCheckBox forBoolean(final Supplier<Boolean> getter, final Consumer<Boolean> setter)
    {
        final JCheckBox checkBox = new JCheckBox("", getter.get());
        checkBox.addActionListener((e) -> setter.accept(checkBox.isSelected()));
        return checkBox;
    }

    public static JComboBox<?> forEnum(final Enum<?>[] values, final Supplier<Enum<?>> getter, final Consumer<Enum<?>> setter)
    {
        final JComboBox<?> comboBox = new JComboBox<>(values);
        comboBox.setSelectedItem(getter.get());
        comboBox.addItemListener(e -> setter.accept((Enum<?>) e.getItem()));
        return comboBox;
    }

    public static JComboBox<?> forEnum(List<Enum<?>> values, Supplier<Enum<?>> getter, Consumer<Enum<?>> setter)
    {
        return forEnum(values.toArray(new Enum<?>[0]), getter, setter);
    }

    public static JComboBox<String> forPracticalEnum(List<String> values, Supplier<String> getter, Consumer<String> setter)
    {
        final JComboBox<String> comboBox = new JComboBox<>(values.toArray(new String[0]));
        comboBox.setSelectedItem(getter.get());
        comboBox.addItemListener(e -> setter.accept((String) e.getItem()));
        return comboBox;
    }

    public static JButton forColor(final Component parent, final Supplier<Color> getter, final Consumer<Color> setter)
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

    public static JSlider forInteger(final Supplier<Integer> getter, final Consumer<Integer> setter, final int minimum, final int maximum)
    {
        final JSlider slider = new JSlider(minimum, maximum, getter.get());
        slider.addChangeListener(e -> setter.accept((slider.getValue())));
        return slider;
    }

    public static JSpinner forIntegerSpinner(final Supplier<Integer> getter, final Consumer<Integer> setter, final int minimum, final int maximum)
    {
        final JSpinner slider = new JSpinner(new SpinnerNumberModel((int) getter.get(), minimum, maximum, 1));
        slider.addChangeListener(e -> setter.accept((Integer) slider.getValue()));
        return slider;
    }

    public static JSlider forDouble(final Supplier<Double> getter, final Consumer<Double> setter, final int minimum, final int maximum)
    {
        final JSlider slider = new JSlider(minimum, maximum, getter.get().intValue());
        slider.addChangeListener(e -> setter.accept((double) slider.getValue()));
        return slider;
    }

    public static JSpinner forDoubleSinner(final Supplier<Double> getter, final Consumer<Double> setter, final double minimum, final double maximum, final double stepSize)
    {
        final JSpinner slider = new JSpinner(new SpinnerNumberModel((double) getter.get(), minimum, maximum, stepSize));
        slider.addChangeListener(e -> setter.accept((double) slider.getValue()));
        return slider;
    }

    public static JButton forFile(final Component parent, final Supplier<File> getter, final Consumer<File> setter)
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

    public static JLabel fallback()
    {
        return new JLabel("Not supported");
    }
}
