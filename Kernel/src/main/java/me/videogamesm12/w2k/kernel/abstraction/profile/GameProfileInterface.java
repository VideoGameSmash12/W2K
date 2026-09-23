package me.videogamesm12.w2k.kernel.abstraction.profile;

import com.google.common.collect.Multimap;

import java.util.UUID;

public interface GameProfileInterface
{
    String w2k$name();

    UUID w2k$uuid();

    Multimap<String, PropertyInterface> w2k$properties();
}
