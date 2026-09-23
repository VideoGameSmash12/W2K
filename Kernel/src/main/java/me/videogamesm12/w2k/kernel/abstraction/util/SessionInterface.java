package me.videogamesm12.w2k.kernel.abstraction.util;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface SessionInterface
{
    String w2k$getUsername();

    String w2k$getUuidString();

    @Nullable
    UUID w2k$getUuid();
}
