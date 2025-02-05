package me.videogamesm12.w2k.drivers.v1_15.mixin.wrapping.network;

import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedPlayerListEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryWrappingMixin implements WrappedPlayerListEntry
{
	@Shadow public abstract GameProfile getProfile();

	@Shadow public abstract int getLatency();

	@Shadow public abstract GameMode getGameMode();

	@Shadow public abstract String getModel();

	@Shadow public abstract Identifier getSkinTexture();

	@Shadow public abstract @Nullable Text getDisplayName();

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
		return ComponentUtils.stringToElement(Text.Serializer.toJson(getDisplayName()));
	}

	@Override
	public int w2k$getLatency()
	{
		return getLatency();
	}

	@Override
	public String w2k$getGameMode()
	{
		return getGameMode().getName();
	}

	@Override
	public String w2k$getModel()
	{
		return getModel();
	}

	@Override
	public String w2k$getSkinIdentifier()
	{
		return getSkinTexture().toString();
	}
}
