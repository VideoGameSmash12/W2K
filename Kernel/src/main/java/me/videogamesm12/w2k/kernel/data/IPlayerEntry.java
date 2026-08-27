package me.videogamesm12.w2k.kernel.data;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;

import java.util.List;

public interface IPlayerEntry
{
    GameProfile w2k$profile();

    JsonElement w2k$displayName();

    int w2k$latency();

    String w2k$gameMode();

    String w2k$model();

    String w2k$skinIdentifier();

    List<Object> w2k$toTableRow();
}
