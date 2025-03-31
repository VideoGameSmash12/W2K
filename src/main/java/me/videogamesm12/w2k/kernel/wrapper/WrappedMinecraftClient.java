package me.videogamesm12.w2k.kernel.wrapper;

import me.videogamesm12.w2k.kernel.wrapper.entity.player.WrappedPlayerEntity;
import me.videogamesm12.w2k.kernel.wrapper.gui.WrappedScreen;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedClientPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface WrappedMinecraftClient
{
	@Nullable WrappedClientPlayNetworkHandler w2k$getNetworkHandler();

	default WrappedClientPlayNetworkHandler w2k$getNonnullNetworkHandler()
	{
		if (!w2k$isNetworkHandlerPresent())
		{
			throw new IllegalStateException("Not connected to a server");
		}

		return Objects.requireNonNull(w2k$getNetworkHandler());
	}

	boolean w2k$isNetworkHandlerPresent();

	@Nullable WrappedPlayerEntity w2k$getPlayer();

	default @NotNull WrappedPlayerEntity w2k$getNonnullPlayer()
	{
		if (w2k$getPlayer() == null)
		{
			throw new IllegalStateException("Not connected to a server");
		}

		return Objects.requireNonNull(w2k$getPlayer());
	}

	void w2k$setScreen(WrappedScreen screen);

	void w2k$queuePreRender(Runnable runnable);

	void w2k$queuePreTick(Runnable runnable);

	void w2k$queuePostTick(Runnable runnable);

	void w2k$scheduleSafeShutdown();
}
