package me.videogamesm12.w2k.kernel.abstraction.profile;

import com.google.common.collect.Multimap;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.UUID;

public interface GameProfileInterface extends ObjectInterface
{
    String w2k$name();

    UUID w2k$uuid();

    Multimap<String, PropertyInterface> w2k$properties();
}
