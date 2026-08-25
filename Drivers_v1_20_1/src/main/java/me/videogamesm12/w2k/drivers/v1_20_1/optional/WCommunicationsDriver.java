package me.videogamesm12.w2k.drivers.v1_20_1.optional;

import me.videogamesm12.w2k.kernel.data.packet.WPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WCommunicationsDriver implements me.videogamesm12.w2k.kernel.driver.base.WCommunicationsDriver
{
    private static final Map<Class<? extends WPacket>, Translator<? extends WPacket>> translatorMap = new HashMap<>();
    private static final Map<String, Translator<?>> translationMap = new HashMap<>();

    private final Map<Integer, Consumer<? extends WPacket>> pendingTransactions = new HashMap<>();

    static
    {
    }

    public WCommunicationsDriver()
    {
        //ClientPlayNetworking.registerGlobalReceiver(new Identifier(""), packetHandler(""))
    }

    @Override
    public void sendPacket(WPacket packet)
    {

    }

    @Override
    public void sendTransactionalPacket(WPacket packet, Consumer<? extends WPacket> consumer)
    {

        pendingTransactions.put(packet.transactionId(), consumer);
    }

    @Override
    public <T extends WPacket> void receivePacket(T packet)
    {
        if (packet.transactional() && pendingTransactions.containsKey(packet.transactionId()))
        {
            //pendingTransactions.get()
        }
    }

    public static <T extends WPacket> void registerTranslator(final Class<T> clazz, final Translator<T> translator)
    {
        translatorMap.put(clazz, translator);
    }

    /*public static <T extends WPacket> Translator<T> getTranslator(final Class<T> clazz)
    {
        return (Translator<T>) translatorMap.get(clazz);
    }*/

    public interface Translator<T extends WPacket>
    {
        T getResult(PacketByteBuf buffer);
    }

    private ClientPlayNetworking.PlayChannelHandler packetHandler(final String identifier)
    {
        return (client, handler, buf, responseSender) -> receivePacket(translationMap.get(identifier).getResult(buf));
    }
}
