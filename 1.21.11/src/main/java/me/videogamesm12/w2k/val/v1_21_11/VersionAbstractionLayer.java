package me.videogamesm12.w2k.val.v1_21_11;

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
import me.videogamesm12.w2k.val.v1_21_11.command.CommandRegistrar;
import me.videogamesm12.w2k.val.v1_21_11.graphics.OverlayRenderDispatcherImpl;
import me.videogamesm12.w2k.val.v1_21_11.protocol.PacketTranslator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VersionAbstractionLayer extends BaseVersionAbstractionLayer<MinecraftClient>
{
    private final NBTConverter<NbtCompound> nbtConverter = new NBTConverter<>(
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
                    return StringNbtReader.readCompound(io.asString(adventureCompound));
                }
                catch (IOException | CommandSyntaxException ex)
                {
                    throw new RuntimeException(ex);
                }
            },
            NbtCompound::toString);
    private final TextConverter<Text> textConverter;
    private final OverlayRenderDispatcherImpl renderDispatcher;
    private final CommandRegistrar commandRegistrar;
    private final PacketTranslator packetTranslator;

    public VersionAbstractionLayer()
    {
        super(MinecraftClient.getInstance());
        this.textConverter = new TextConverter<>()
        {
            private final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();
            private final RegistryWrapper.WrapperLookup backupLookup = BuiltinRegistries.createWrapperLookup();

            @Override
            public Component nativeToAdventure(Text text)
            {
                return TextCodecs.CODEC.encodeStart(getLookupOrElse(backupLookup).getOps(JsonOps.INSTANCE), text)
                        .mapOrElse(ComponentUtils::deserializeComponent,
                                error -> Component.text(text.getString()).append(Component.text(" (!)").hoverEvent(HoverEvent.showText(Component.text(error.message())))));
            }

            @Override
            public Text adventureToNative(Component component)
            {
                return TextCodecs.CODEC.parse(getLookupOrElse(backupLookup).getOps(JsonOps.INSTANCE), ComponentUtils.serializeComponent(component)).getOrThrow();
            }

            @Override
            public String adventureToString(Component component, boolean useNative)
            {
                return useNative ? adventureToNative(component).getString() : plainText.serialize(component);
            }

            @Override
            public String jsonToString(JsonElement component)
            {
                return Objects.requireNonNull(TextCodecs.CODEC.parse(getLookupOrElse(backupLookup).getOps(JsonOps.INSTANCE), component)).getOrThrow().getString();
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
        C2SPlayChannelEvents.REGISTER.register(((handler, sender, client, channels) -> W2K.getEventBus().post(new RegisterPluginMessageEvent(client))));
        AttackEntityCallback.EVENT.register((player, world, hand, target, nullableHitResult) ->
        {
            if (player instanceof ClientPlayerEntity clientPlayer)
            {
                final EntityInteractionEvent event = new EntityInteractionEvent((ClientPlayerEntityInterface) clientPlayer, (EntityInterface) target, true);
                W2K.getEventBus().post(event);

                if (event.isCancelled())
                {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, target, nullableHitResult) ->
        {
            if (player instanceof ClientPlayerEntity clientPlayer)
            {
                final EntityInteractionEvent event = new EntityInteractionEvent((ClientPlayerEntityInterface) clientPlayer, (EntityInterface) target, false);
                W2K.getEventBus().post(event);

                if (event.isCancelled())
                {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }

    @Override
    public Optional<PlayNetworkHandlerInterface> networkHandler()
    {
        return Optional.ofNullable((PlayNetworkHandlerInterface) minecraft.getNetworkHandler());
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
        return Optional.ofNullable((ClientWorldInterface) minecraft.world);
    }

    @Override
    public Optional<EntityInterface> getTargetedEntity()
    {
        return Optional.ofNullable((EntityInterface) minecraft.targetedEntity);
    }

    @Override
    public EntityInterface getTargetedEntityUnsafe()
    {
        return (EntityInterface) minecraft.targetedEntity;
    }

    @Override
    public SessionInterface getSession()
    {
        return (SessionInterface) minecraft.getSession();
    }

    @Override
    public void closeCurrentScreen()
    {
        minecraft.setScreen(null);
    }

    @Override
    public void execute(Runnable runnable)
    {
        minecraft.executeTask(runnable);
    }

    @Override
    public void scheduleShutdown()
    {
        minecraft.scheduleStop();
    }

    @Override
    public TextConverter<Text> text()
    {
        return textConverter;
    }

    @Override
    public NBTConverter<NbtCompound> nbt()
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

    private RegistryWrapper.WrapperLookup getLookupOrElse(RegistryWrapper.WrapperLookup lookup)
    {
        return minecraft.world != null ? minecraft.world.getRegistryManager() : lookup;
    }
}
