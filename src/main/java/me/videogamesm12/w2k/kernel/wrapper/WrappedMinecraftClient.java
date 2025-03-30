package me.videogamesm12.w2k.kernel.wrapper;

import me.videogamesm12.w2k.kernel.wrapper.network.WrappedClientPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;

public interface WrappedMinecraftClient
{
	@Nullable WrappedClientPlayNetworkHandler w2k$getNetworkHandler();

	boolean w2k$isNetworkHandlerPresent();
}
