package me.videogamesm12.w2k.val.v1_21_11.wrappers.network;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayerListEntryInterface;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
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
    public abstract SkinTextures getSkinTextures();

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
        final Text displayName = getDisplayName() != null ? getDisplayName() : Text.literal(getProfile().name());

        if (cachedDisplayName == null || displayName.hashCode() != displayNameHash)
        {
            cachedDisplayName = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, displayName).result().orElseThrow();
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
        return getSkinTextures().comp_1629().asString();
    }

    @Override
    public String w2k$skinIdentifier()
    {
        return getSkinTextures().comp_1626().comp_3627().toString();
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
