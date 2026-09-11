package me.videogamesm12.w2k.kernel.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public interface IEntitySelector
{
    List<IEntityEntry> w2k$getClientEntities();
}
