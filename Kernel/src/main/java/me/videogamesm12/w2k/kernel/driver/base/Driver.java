package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.command.WCommand;
import me.videogamesm12.w2k.kernel.data.action.AbstractAction;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.fabricmc.loader.api.ModContainer;

import java.util.List;
import java.util.Map;

public abstract class Driver
{
    private ModContainer mod = null;

    public abstract List<WModule> modules();

    public abstract List<WCommand> commands();

    public void init()
    {
    }

    public final Driver mod(ModContainer mod)
    {
        this.mod = mod;
        return this;
    }

    public final ModContainer mod()
    {
        return mod;
    }

    class Meta
    {
        String name;

        String[] dependencies;
    }
}
