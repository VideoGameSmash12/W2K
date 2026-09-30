package me.videogamesm12.w2k.val.v26_1_x.wrappers.network;

import me.videogamesm12.w2k.kernel.abstraction.network.DataQueryHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import net.kyori.adventure.text.Component;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerWrapper implements PlayNetworkHandlerInterface
{
    @Shadow
    public abstract Collection<PlayerInfo> getOnlinePlayers();

    @Shadow
    public abstract void sendChat(String content);

    @Shadow
    public abstract void sendCommand(String command);

    @Shadow
    public abstract Connection getConnection();

    @Shadow
    @Final
    private DebugQueryHandler debugQueryHandler;

    @Override
    public void w2k$sendChatMessage(String message)
    {
        sendChat(message);
    }

    @Override
    public void w2k$sendCommand(String command)
    {
        sendCommand(command);
    }

    @Override
    public void w2k$disconnect(Component reason)
    {
        getConnection().disconnect((net.minecraft.network.chat.Component) w2k$val().text().adventureToNative(reason));
    }

    @Override
    public List<PlayerInfo> w2k$getOnlinePlayers()
    {
        return getOnlinePlayers().stream().toList();
    }

    @Override
    public DataQueryHandlerInterface w2k$getDataQueryHandler()
    {
        return (DataQueryHandlerInterface) debugQueryHandler;
    }
}
