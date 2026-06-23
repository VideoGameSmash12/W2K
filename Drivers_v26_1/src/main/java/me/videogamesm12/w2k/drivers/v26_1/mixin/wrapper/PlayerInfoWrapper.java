package me.videogamesm12.w2k.drivers.v26_1.mixin.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IPlayerEntry;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.GameType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.List;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoWrapper implements IPlayerEntry
{
    @Shadow
    public abstract GameProfile getProfile();

    @Shadow
    @Nullable
    public abstract Component getTabListDisplayName();

    @Shadow
    private int latency;

    @Shadow
    public abstract GameType getGameMode();

    @Shadow
    public abstract PlayerSkin getSkin();

    @Unique
    private JsonElement cachedDisplayName = null;
    @Unique
    private int displayNameHash = 0;

    @Override
    public GameProfile w2k$profile()
    {
        return getProfile();
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
        return getGameMode().getName();
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
                w2k$profile().name(),                                                                                   // Username
                W2K.getInstance().getDriverManager().getVersionBridge().textToString(w2k$displayName()),                // Display Name
                w2k$profile().id(),                                                                                     // UUID
                w2k$latency(),                                                                                          // Ping
                w2k$gameMode(),                                                                                         // Gamemode
                w2k$model(),                                                                                            // Skin Model
                w2k$skinIdentifier()                                                                                    // Skin Identifier
        );
    }
}