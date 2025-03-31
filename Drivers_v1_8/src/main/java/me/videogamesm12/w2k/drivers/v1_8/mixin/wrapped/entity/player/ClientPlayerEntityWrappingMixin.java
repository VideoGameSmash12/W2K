package me.videogamesm12.w2k.drivers.v1_8.mixin.wrapped.entity.player;

import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.wrapper.entity.player.WrappedPlayerEntity;
import net.kyori.adventure.text.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityWrappingMixin implements WrappedPlayerEntity
{
	@Shadow public abstract void requestRespawn();

	@Shadow public abstract void sendMessage(Text par1);

	@Shadow protected MinecraftClient client;

	@Shadow public abstract void sendChatMessage(String string);

	@Override
	public void w2k$requestRespawn()
	{
		requestRespawn();
	}

	@Override
	public void w2k$displayMessage(Component component, boolean actionBar)
	{
		final Text converted = Text.Serializer.deserialize(ComponentUtils.serializeComponent(component).toString());

		if (actionBar)
		{
			this.client.inGameHud.setOverlayMessage(converted.asFormattedString(), false);
		}
		else
		{
			sendMessage(converted);
		}
	}

	@Override
	public void w2k$sendMessage(String message)
	{
		sendChatMessage(message);
	}

	@Override
	public void w2k$runCommand(String message)
	{
		sendChatMessage(message);
	}
}
