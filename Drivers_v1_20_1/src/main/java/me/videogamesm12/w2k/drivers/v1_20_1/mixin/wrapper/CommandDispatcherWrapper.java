package me.videogamesm12.w2k.drivers.v1_20_1.mixin.wrapper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import me.videogamesm12.w2k.drivers.v1_20_1.required.W120CommandDriver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandDispatcher.class)
public class CommandDispatcherWrapper<S> implements W120CommandDriver.DispatcherHook<S>
{
    @Shadow
    @Final
    private RootCommandNode<S> root;

    @Override
    public void w2k$register(final CommandNode<S> source)
    {
        root.addChild(source);
    }
}
