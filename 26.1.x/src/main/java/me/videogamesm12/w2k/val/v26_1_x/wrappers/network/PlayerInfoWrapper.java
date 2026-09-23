package me.videogamesm12.w2k.val.v26_1_x.wrappers.network;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import me.videogamesm12.w2k.kernel.abstraction.profile.GameProfileInterface;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoWrapper implements PlayerListEntryInterface
{
    @Shadow
    public abstract GameProfile getProfile();

    @Shadow
    private int latency;

    @Shadow
    @Nullable
    public abstract Component getTabListDisplayName();

    @Shadow
    public abstract GameType getGameMode();

    @Shadow
    public abstract PlayerSkin getSkin();

    @Unique
    private JsonElement cachedDisplayName = null;
    @Unique
    private int displayNameHash = 0;

    @Override
    public GameProfileInterface w2k$profile()
    {
        return GameProfileInterface.class.cast(getProfile());
    }

    @Override
    public String w2k$username()
    {
        return getProfile().name();
    }

    @Override
    public UUID w2k$uuid()
    {
        return getProfile().id();
    }

    @Override
    public JsonElement w2k$displayName()
    {
        final Component displayName = getTabListDisplayName() != null ? getTabListDisplayName() : Component.literal(getProfile().name());

        if (cachedDisplayName == null || displayName.hashCode() != displayNameHash)
        {
            cachedDisplayName = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, displayName).getOrThrow();
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
        return getGameMode().name();
    }

    @Override
    public String w2k$model()
    {
        return getSkin().model().getSerializedName();
    }

    @Override
    public String w2k$skinIdentifier()
    {
        return getSkin().body().texturePath().toString();
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
