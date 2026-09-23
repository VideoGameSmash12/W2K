package me.videogamesm12.w2k.val.v26_1_x;

import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.w2k.kernel.abstraction.conversion.NBTConverter;
import me.videogamesm12.w2k.kernel.abstraction.conversion.TextConverter;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.SessionInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientWorldInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.entity.EntityInteractionEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.event.network.DisconnectEvent;
import me.videogamesm12.w2k.kernel.event.network.JoinEvent;
import me.videogamesm12.w2k.kernel.event.network.RegisterPluginMessageEvent;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.val.v26_1_x.command.CommandRegistrar;
import me.videogamesm12.w2k.val.v26_1_x.graphics.OverlayRenderDispatcherImpl;
import me.videogamesm12.w2k.val.v26_1_x.protocol.PacketTranslator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ClientboundPlayChannelEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.InteractionResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class VersionAbstractionLayer extends BaseVersionAbstractionLayer<Minecraft>
{
    private final NBTConverter<CompoundTag> nbtConverter = new NBTConverter<>(
            (io, nativeCompound) ->
            {
                try
                {
                    return io.asCompound(nativeCompound.toString());
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
            },
            (io, adventureCompound) ->
            {
                try
                {
                    return TagParser.parseCompoundFully(io.asString(adventureCompound));
                }
                catch (IOException | CommandSyntaxException ex)
                {
                    throw new RuntimeException(ex);
                }
            },
            CompoundTag::toString);
    private final TextConverter<net.minecraft.network.chat.Component> textConverter;
    private final OverlayRenderDispatcherImpl renderDispatcher;
    private final CommandRegistrar commandRegistrar;
    private final PacketTranslator packetTranslator;

    public VersionAbstractionLayer()
    {
        super(Minecraft.getInstance());
        this.textConverter = new TextConverter<>()
        {
            private final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();
            private final HolderLookup.Provider backupLookup = VanillaRegistries.createLookup();

            @Override
            public Component nativeToAdventure(net.minecraft.network.chat.Component text)
            {
                return ComponentSerialization.CODEC.encodeStart(getLookupOrElse(backupLookup).createSerializationContext(JsonOps.INSTANCE), text)
                        .mapOrElse(ComponentUtils::deserializeComponent,
                                error -> Component.text(text.getString()).append(Component.text(" (!)").hoverEvent(HoverEvent.showText(Component.text(error.message())))));
            }

            @Override
            public net.minecraft.network.chat.Component adventureToNative(Component component)
            {
                return ComponentSerialization.CODEC.parse(getLookupOrElse(backupLookup).createSerializationContext(JsonOps.INSTANCE), ComponentUtils.serializeComponent(component)).getOrThrow();
            }

            @Override
            public String adventureToString(Component component, boolean useNative)
            {
                return useNative ? adventureToNative(component).getString() : plainText.serialize(component);
            }

            @Override
            public String jsonToString(JsonElement component)
            {
                return ComponentSerialization.CODEC.parse(getLookupOrElse(backupLookup).createSerializationContext(JsonOps.INSTANCE), component).getOrThrow().getString();
            }
        };
        this.renderDispatcher = new OverlayRenderDispatcherImpl();
        this.commandRegistrar = new CommandRegistrar();
        this.packetTranslator = new PacketTranslator();
    }

    @Override
    public void setup()
    {
        // Set up handlers for events provided by the Fabric API
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> W2K.getEventBus().post(new ClientStartedEvent(client)));
        ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> W2K.getEventBus().post(new ClientStoppedEvent(client)));
        ClientPlayConnectionEvents.DISCONNECT.register((connection, client) -> W2K.getEventBus().post(new DisconnectEvent(connection, client)));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> W2K.getEventBus().post(new JoinEvent(handler, sender, client)));
        ClientboundPlayChannelEvents.REGISTER.register(((_, _, client, _) -> W2K.getEventBus().post(new RegisterPluginMessageEvent(client))));
        AttackEntityCallback.EVENT.register((player, _, _, target, _) ->
        {
            if (player instanceof LocalPlayer localPlayer)
            {
                final EntityInteractionEvent event = new EntityInteractionEvent((ClientPlayerEntityInterface) localPlayer, (EntityInterface) target, true);
                W2K.getEventBus().post(event);

                if (event.isCancelled())
                {
                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, _, _, target, _) ->
        {
            if (player instanceof LocalPlayer localPlayer)
            {
                final EntityInteractionEvent event = new EntityInteractionEvent((ClientPlayerEntityInterface) localPlayer, (EntityInterface) target, false);
                W2K.getEventBus().post(event);

                if (event.isCancelled())
                {
                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.PASS;
        });
    }

    @Override
    public Optional<PlayNetworkHandlerInterface> networkHandler()
    {
        return Optional.ofNullable((PlayNetworkHandlerInterface) minecraft.getConnection());
    }

    @Override
    public Optional<ClientPlayerEntityInterface> getLocalPlayer()
    {
        return Optional.ofNullable((ClientPlayerEntityInterface) minecraft.player);
    }

    @Override
    public ClientPlayerEntityInterface getLocalPlayerUnsafe()
    {
        return (ClientPlayerEntityInterface) minecraft.player;
    }

    @Override
    public Optional<ClientWorldInterface> getLocalWorld()
    {
        return Optional.ofNullable((ClientWorldInterface) minecraft.level);
    }

    @Override
    public Optional<EntityInterface> getTargetedEntity()
    {
        return Optional.ofNullable((EntityInterface) minecraft.crosshairPickEntity);
    }

    @Override
    public EntityInterface getTargetedEntityUnsafe()
    {
        return (EntityInterface) minecraft.crosshairPickEntity;
    }

    @Override
    public SessionInterface getSession()
    {
        return (SessionInterface) minecraft.getUser();
    }

    @Override
    public void closeCurrentScreen()
    {
        minecraft.setScreen(null);
    }

    @Override
    public void execute(Runnable runnable)
    {
        minecraft.schedule(runnable);
    }

    @Override
    public void scheduleShutdown()
    {
        minecraft.stop();
    }

    @Override
    public TextConverter<net.minecraft.network.chat.Component> text()
    {
        return textConverter;
    }

    @Override
    public NBTConverter<CompoundTag> nbt()
    {
        return nbtConverter;
    }

    @Override
    public OverlayRenderDispatcherImpl renderDispatcher()
    {
        return renderDispatcher;
    }

    @Override
    public List<String> getClientOverview()
    {
        return List.of("Due to internal changes in how Minecraft handles the F3 overlay and the complications that come with that, this information is currently unavailable in this build for this version of Minecraft. Sorry!");
    }

    @Override
    public CommandRegistrar commandRegistrar()
    {
        return commandRegistrar;
    }

    @Override
    public PacketTranslator packetTranslator()
    {
        return packetTranslator;
    }

    private HolderLookup.Provider getLookupOrElse(HolderLookup.Provider lookup)
    {
        return minecraft.level != null ? minecraft.level.registryAccess() : lookup;
    }
}
