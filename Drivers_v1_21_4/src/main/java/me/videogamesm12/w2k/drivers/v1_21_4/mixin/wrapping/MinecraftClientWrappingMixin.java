package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapping;

import me.videogamesm12.w2k.kernel.wrapper.WrappedMinecraftClient;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientWrappingMixin implements WrappedMinecraftClient
{
	@Shadow @Nullable public abstract ClientPlayNetworkHandler getNetworkHandler();

	@Override
	public WrappedClientPlayNetworkHandler w2k$getNetworkHandler()
	{
		ClientPlayNetworkHandler handler = getNetworkHandler();
		return handler != null ? (WrappedClientPlayNetworkHandler) handler : null;
	}
}
