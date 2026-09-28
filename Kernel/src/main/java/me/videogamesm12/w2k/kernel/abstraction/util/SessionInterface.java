package me.videogamesm12.w2k.kernel.abstraction.util;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface SessionInterface extends ObjectInterface
{
    String w2k$getUsername();

    String w2k$getUuidString();

    @Nullable
    UUID w2k$getUuid();
}
