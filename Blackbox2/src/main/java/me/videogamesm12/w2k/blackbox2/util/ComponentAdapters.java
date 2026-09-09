package me.videogamesm12.w2k.blackbox2.util;

import com.google.common.base.Preconditions;
import me.videogamesm12.w2k.kernel.module.setting.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ComponentAdapters
{
    private static final Map<String, ComponentAdapter<?, ?>> adapterMap = new HashMap<>();
    //--
    public static final ComponentAdapter<?, JLabel> GENERIC_UNSUPPORTED = register(Object.class, (ignored) -> new JLabel("Not supported"));

    static
    {
        // Internally register our settings
        register(BooleanSetting.class, bool -> JComponents.createCheckbox(bool::get, bool::set));
        register(ColorSetting.class, (color, parent) -> JComponents.createColorPicker(parent, color::get, color::set));
        register(StringSetting.class, string -> JComponents.createTextField(string::get, string::set));
        register(IntegerSetting.class, integer -> integer.isSpinner() ?
                JComponents.createSpinner(integer::get, integer::set, integer.getMinimum(), integer.getMaximum()) :
                JComponents.createSlider(integer::get, integer::set, integer.getMinimum(), integer.getMaximum()));
        register(LongSetting.class, longS -> JComponents.createSpinner(longS::get, longS::set, longS.getMinimum(), longS.getMaximum()));
        register(FileSetting.class, (file, parent) -> JComponents.createFilePicker(parent, file::get, file::set));
    }

    public static <T, J extends JComponent> ComponentAdapter<T, J> register(final Class<T> clazz, final Function<T, J> creator)
    {
        final ComponentAdapter<T, J> instance = new ComponentAdapter<>(clazz, (inst, ignored) -> creator.apply(inst), false);
        adapterMap.put(clazz.getName(), instance);
        return instance;
    }

    public static <T, J extends JComponent> ComponentAdapter<T, J> register(final Class<T> clazz, final BiFunction<T, JDialog, J> creator)
    {
        final ComponentAdapter<T, J> instance = new ComponentAdapter<>(clazz, creator, true);
        adapterMap.put(clazz.getName(), instance);
        return instance;
    }

    public static <T, J extends JComponent> void register(final ComponentAdapter<T, J> adapter)
    {
        adapterMap.put(adapter.getObjectClass().toGenericString(), adapter);
    }

    public static <T, J extends JComponent> ComponentAdapter<T, J> get(T instance)
    {
        Preconditions.checkArgument(adapterMap.containsKey(instance.getClass().toGenericString()), "This type isn't registered");
        return (ComponentAdapter<T, J>) adapterMap.get(instance.getClass().toGenericString());
    }
}
