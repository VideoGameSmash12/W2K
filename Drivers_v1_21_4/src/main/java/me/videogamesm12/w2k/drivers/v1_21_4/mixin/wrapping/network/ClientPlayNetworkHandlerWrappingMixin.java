package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapping.network;

import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedClientPlayNetworkHandler;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedPlayerListEntry;
import net.kyori.adventure.text.Component;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerWrappingMixin implements WrappedClientPlayNetworkHandler
{
	@Shadow public abstract Collection<PlayerListEntry> getPlayerList();

	@Shadow public abstract ClientConnection getConnection();

	@Shadow public abstract DynamicRegistryManager.Immutable getRegistryManager();

	@Override
	public List<WrappedPlayerListEntry> w2k$getPlayerList()
	{
		return getPlayerList().stream().map(entry -> (WrappedPlayerListEntry) entry).toList();
	}

	@Override
	public void w2k$disconnect(Component reason)
	{
		getConnection().disconnect(Text.Serialization.fromJsonTree(ComponentUtils.serializeComponent(reason), getRegistryManager()));
	}
}
