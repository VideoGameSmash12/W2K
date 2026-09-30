package me.videogamesm12.w2k.val.v1_21_11.wrappers.network;

import me.videogamesm12.w2k.kernel.abstraction.network.DataQueryHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import net.kyori.adventure.text.Component;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerWrapper implements PlayNetworkHandlerInterface
{
    @Shadow
    public abstract Collection<PlayerListEntry> getPlayerList();

    @Shadow
    public abstract void sendChatMessage(String string);

    @Shadow
    public abstract void sendChatCommand(String string);

    @Shadow
    public abstract ClientConnection getConnection();

    @Shadow
    @Final
    private DataQueryHandler dataQueryHandler;

    @Override
    public void w2k$sendChatMessage(String message)
    {
        sendChatMessage(message);
    }

    @Override
    public void w2k$sendCommand(String message)
    {
        sendChatCommand(message);
    }

    @Override
    public void w2k$disconnect(Component reason)
    {
        getConnection().disconnect((Text) w2k$val().text().adventureToNative(reason));
    }

    @Override
    public List<PlayerListEntry> w2k$getOnlinePlayers()
    {
        return getPlayerList().stream().toList();
    }

    @Override
    public DataQueryHandlerInterface w2k$getDataQueryHandler()
    {
        return (DataQueryHandlerInterface) dataQueryHandler;
    }
}
