package me.videogamesm12.w2k.kernel.command;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public abstract class WCommand
{
    private final String name;
    private final String usage;

    protected WCommand()
    {
        if (!getClass().isAnnotationPresent(Parameters.class))
        {
            throw new IllegalArgumentException("Commands must have the Parameters class to be initialized this way");
        }

        final Parameters parameters = getClass().getAnnotation(Parameters.class);

        this.name       = parameters.name();
        this.usage      = parameters.usage();
    }

    public abstract boolean executeCommand(String commandLabel, String[] args);

    public final void msg(@NotNull Component component)
    {
        Objects.requireNonNull(component);
        W2K.getInstance().getDriverManager().getVersionBridge().displayMessage(component);
    }
}
