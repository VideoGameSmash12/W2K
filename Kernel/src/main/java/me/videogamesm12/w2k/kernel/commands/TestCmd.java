package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.command.*;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Parameters(name = "test", usage = "No point here")
public class TestCmd extends WCommand
{
    public TestCmd()
    {
        super("test");
    }

    @ExecutionPath
    public void rootCommand()
    {
        msg(Objects.requireNonNull(BuildMetadata.getMetadataFromMod("w2k")).toComponent());
    }
}
