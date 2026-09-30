package me.videogamesm12.w2k.val.v1_20_1.wrappers.network;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;
import net.kyori.adventure.text.Component;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryWrapper implements PlayerListEntryInterface
{
    @Shadow
    public abstract GameProfile getProfile();

    @Shadow
    private int latency;

    @Shadow
    public abstract GameMode getGameMode();

    @Shadow
    @Nullable
    public abstract Text getDisplayName();

    @Shadow
    public abstract Identifier getSkinTexture();

    @Shadow
    public abstract String getModel();

    @Unique
    private JsonElement cachedDisplayName = null;
    @Unique
    private int displayNameHash = 0;

    @Override
    public GameProfileInterface w2k$profile()
    {
        return (GameProfileInterface) getProfile();
    }

    @Override
    public String w2k$username()
    {
        return w2k$profile().w2k$name();
    }

    @Override
    public UUID w2k$uuid()
    {
        return w2k$profile().w2k$uuid();
    }

    @Override
    public JsonElement w2k$displayName()
    {
        final Text displayName = getDisplayName() != null ? getDisplayName() : Text.literal(getProfile().getName());

        if (cachedDisplayName == null || displayName.hashCode() != displayNameHash)
        {
            cachedDisplayName = Text.Serializer.toJsonTree(displayName);
            displayNameHash = cachedDisplayName.hashCode();
        }

        return cachedDisplayName;
    }

    @Override
    public int w2k$latency()
    {
        return latency;
    }

    @Override
    public String w2k$gameMode()
    {
        return getGameMode().getName();
    }

    @Override
    public String w2k$model()
    {
        return getModel();
    }

    @Override
    public String w2k$skinIdentifier()
    {
        return getSkinTexture().toString();
    }

    @Override
    public List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$username(),                                         // Username
                w2k$val().text().jsonToString(w2k$displayName()),       // Display Name
                w2k$uuid(),                                             // UUID
                w2k$latency(),                                          // Ping
                w2k$gameMode(),                                         // Gamemode
                w2k$model(),                                            // Skin Model
                w2k$skinIdentifier()                                    // Skin Identifier
        );
    }
}
