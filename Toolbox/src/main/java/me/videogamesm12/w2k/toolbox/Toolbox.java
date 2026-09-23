package me.videogamesm12.w2k.toolbox;

import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.driver.base.Driver;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.toolbox.commands.*;
import me.videogamesm12.w2k.toolbox.modules.*;

import java.util.Arrays;
import java.util.List;

public class Toolbox extends Driver
{
    private List<WModule> modules = null;
    private List<WCommand> commands = null;

    @Override
    public void init()
    {
        modules = Arrays.asList(
                new AntiLockup(),
                new BanHammer(),
                new EaglerPrint(),
                new QueryLogger(),
                new TargetHighlighter(),
                new TPSOverlay(),
                new Watermark());

        commands = Arrays.asList(
                new NameCmd(),
                new UuidCmd(),
                new PremiumCmd(),
                new DumpCmd(),
                new QueryCmd(),
                new LKillCmd(),
                new ProfileCmd());
    }

    @Override
    public List<WModule> modules()
    {
        return modules;
    }

    @Override
    public List<WCommand> commands()
    {
        return commands;
    }
}
