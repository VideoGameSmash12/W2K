package me.videogamesm12.w2k.blackbox2.util;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class ComponentAdapter<T, J extends JComponent>
{
    @Getter
    @NonNull
    private final Class<T> objectClass;
    @NonNull
    private final BiFunction<T, JDialog, J> adapter;
    @Getter
    private final boolean showsPopup;

    public J createOption(final T instance)
    {
        Preconditions.checkState(!showsPopup, "This needs a parent window to work!");

        return adapter.apply(instance, null);
    }

    public J createOption(final T instance, final JDialog parent)
    {
        return adapter.apply(instance, parent);
    }
}
