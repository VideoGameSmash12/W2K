package me.videogamesm12.w2k.drivers.v1_20_1.mixin.wrapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IPlayerEntry;
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

@Mixin(PlayerListEntry.class)
public abstract class PlayerEntryWrapper implements IPlayerEntry
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
    public GameProfile w2k$profile()
    {
        return getProfile();
    }

    @Override
    public JsonElement w2k$displayName()
    {
        final Text displayName = getDisplayName() != null ? getDisplayName() : Text.literal(getProfile().getName());

        final JsonObject fallback = new JsonObject();
        fallback.addProperty("text", getProfile().getName());

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
                w2k$profile().getName(),                                                                                // Username
                W2K.getInstance().getDriverManager().getVersionBridge().textToString(w2k$displayName()),                // Display Name
                w2k$profile().getId(),                                                                                  // UUID
                w2k$latency(),                                                                                          // Ping
                w2k$gameMode(),                                                                                         // Gamemode
                w2k$model(),                                                                                            // Skin Model
                w2k$skinIdentifier()                                                                                    // Skin Identifier
        );
    }
}