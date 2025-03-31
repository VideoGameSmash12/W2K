package me.videogamesm12.w2k.drivers.v1_8.mixin.wrapped.network;

import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedClientPlayNetworkHandler;
import me.videogamesm12.w2k.kernel.wrapper.network.WrappedPlayerListEntry;
import net.kyori.adventure.text.Component;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerWrappingMixin implements WrappedClientPlayNetworkHandler
{
	@Shadow public abstract Collection<PlayerListEntry> getPlayerList();

	@Shadow @Final private ClientConnection connection;

	@Override
	public List<WrappedPlayerListEntry> w2k$getPlayerList()
	{
		return getPlayerList().stream().map(entry -> (WrappedPlayerListEntry) entry).collect(Collectors.toList());
	}

	@Override
	public void w2k$disconnect(Component reason)
	{
		connection.disconnect(Text.Serializer.deserialize(ComponentUtils.serializeComponent(reason).toString()));
	}
}
