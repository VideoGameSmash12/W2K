package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapping.network;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.drivers.v1_21_4.required.W1212VersionBridgeDriver;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedPlayerListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryWrappingMixin implements WrappedPlayerListEntry
{
	@Shadow public abstract GameProfile getProfile();

	@Shadow public abstract @Nullable Text getDisplayName();

	@Shadow public abstract int getLatency();

	@Shadow public abstract GameMode getGameMode();

	@Shadow public abstract SkinTextures getSkinTextures();

	@Override
	public String w2k$getPlayerName()
	{
		return getProfile().getName();
	}

	@Override
	public @Nullable UUID w2k$getPlayerUuid()
	{
		return getProfile().getId();
	}

	@Override
	public JsonElement w2k$getDisplayName()
	{
		return ComponentUtils.stringToElement(Text.Serialization.toJsonString(getDisplayName() != null ?
				getDisplayName() : Text.literal(w2k$getPlayerName()), MinecraftClient.getInstance().getNetworkHandler() != null ?
				MinecraftClient.getInstance().getNetworkHandler().getRegistryManager() : W1212VersionBridgeDriver.getWrapperLookup()));
	}

	@Override
	public int w2k$getLatency()
	{
		return getLatency();
	}

	@Override
	public String w2k$getGameMode()
	{
		return getGameMode() != null ? getGameMode().getName() : "";
	}

	@Override
	public String w2k$getModel()
	{
		return getSkinTextures().model().getName();
	}

	@Override
	public String w2k$getSkinIdentifier()
	{
		return getSkinTextures().texture().toString();
	}
}
