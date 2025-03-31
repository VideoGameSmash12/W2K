package me.videogamesm12.w2k.drivers.v1_21_4.mixin.wrapping.entity.player;

import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.entity.player.WrappedPlayerEntity;
import net.kyori.adventure.text.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityWrappingMixin implements WrappedPlayerEntity
{
	@Shadow public abstract void requestRespawn();

	@Final @Shadow protected MinecraftClient client;

	@Shadow public abstract void sendMessage(Text message, boolean overlay);

	@Shadow @Final public ClientPlayNetworkHandler networkHandler;

	@Override
	public void w2k$requestRespawn()
	{
		requestRespawn();
	}

	@Override
	public void w2k$displayMessage(Component component, boolean actionBar)
	{
		sendMessage(Text.Serialization.fromJsonTree(ComponentUtils.serializeComponent(component),
				networkHandler.getRegistryManager()), actionBar);
	}

	@Override
	public void w2k$sendMessage(String message)
	{
		networkHandler.sendChatMessage(message);
	}

	@Override
	public void w2k$runCommand(String command)
	{
		networkHandler.sendChatCommand(command);
	}
}
