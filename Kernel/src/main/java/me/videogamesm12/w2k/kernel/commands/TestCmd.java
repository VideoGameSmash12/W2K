package me.videogamesm12.w2k.kernel.commands;

import me.videogamesm12.w2k.kernel.command.*;
import net.kyori.adventure.text.Component;

@Parameters(name = "test", usage = "No point here")
public class TestCmd extends WCommand
{
    @ExecutionPath({"tree1", "tree2", "tree3"})
    public void treeWithNoArguments()
    {
        msg(Component.text("This just executed /test tree1 tree2 tree3!"));
    }

    @ExecutionPath({"argtree1", "argtree2", "<greedy|w2k:greedy_string>"})
    public void treeWithGreedyArgument(final String greedy)
    {
        msg(Component.text("This just executed /test argtree1 argtree2 " + greedy + "!"));
    }

    @ExecutionPath
    public void rootCommand()
    {
        msg(Component.text("/test!"));
    }
}
