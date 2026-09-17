package me.videogamesm12.w2k.val.v1_20_1;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.w2k.kernel.abstraction.conversion.NBTConverter;
import me.videogamesm12.w2k.kernel.abstraction.conversion.TextConverter;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.util.SessionInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.entity.EntityInteractionEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.event.network.DisconnectEvent;
import me.videogamesm12.w2k.kernel.event.network.JoinEvent;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.kernel.util.VersionUtils;
import me.videogamesm12.w2k.val.v1_20_1.command.CommandRegistrar;
import me.videogamesm12.w2k.val.v1_20_1.graphics.OverlayRenderDispatcherImpl;
import me.videogamesm12.w2k.val.v1_20_1.mixin.DebugHudAccessor;
import me.videogamesm12.w2k.val.v1_20_1.mixin.InGameHudAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.io.IOException;
import java.util.*;

@SuppressWarnings("unchecked")
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
                    return new StringNbtReader(new StringReader(io.asString(adventureCompound))).parseCompound();
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

    public VersionAbstractionLayer()
    {
        super(MinecraftClient.getInstance());
        this.textConverter = new TextConverter<>()
        {
            private final PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

            @Override
            public Component nativeToAdventure(Text text)
            {
                return ComponentUtils.deserializeComponent(Text.Serializer.toJsonTree(text));
            }

            @Override
            public Text adventureToNative(Component component)
            {
                return Text.Serializer.fromJson(ComponentUtils.serializeComponent(component));
            }

            @Override
            public String adventureToString(Component component, boolean useNative)
            {
                return useNative ? adventureToNative(component).getString() : plainText.serialize(component);
            }

            @Override
            public String jsonToString(JsonElement component)
            {
                return Objects.requireNonNull(Text.Serializer.fromJson(component)).getString();
            }
        };
        this.renderDispatcher = new OverlayRenderDispatcherImpl();
        this.commandRegistrar = new CommandRegistrar();
    }

    @Override
    public void setup()
    {
        // Set up handlers for events provided by the Fabric API
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> W2K.getEventBus().post(new ClientStartedEvent(client)));
        ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> W2K.getEventBus().post(new ClientStoppedEvent(client)));
        ClientPlayConnectionEvents.DISCONNECT.register((connection, client) -> W2K.getEventBus().post(new DisconnectEvent(connection, client)));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> W2K.getEventBus().post(new JoinEvent(handler, sender, client)));
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
    public Optional<ClientPlayerEntity> getLocalPlayer()
    {
        return Optional.ofNullable(minecraft.player);
    }

    @Override
    public Optional<Entity> getTargetedEntity()
    {
        return Optional.ofNullable(minecraft.targetedEntity);
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
    public Optional<ClientWorld> getLocalWorld()
    {
        return Optional.ofNullable(minecraft.world);
    }

    @Override
    public void closeCurrentScreen()
    {
        minecraft.setScreen(null);
    }

    @Override
    public void execute(Runnable runnable)
    {
        minecraft.execute(runnable);
    }

    @Override
    public void scheduleShutdown()
    {
        minecraft.scheduleStop();
    }

    @Override
    public List<String> getClientOverview()
    {
        final List<String> fallback = List.of(String.format("Minecraft %1$s (%1$s/%2$s)", VersionUtils.getGameVersion().getId(), ClientBrandRetriever.getClientModName()),
                minecraft.fpsDebugString);

        if (minecraft.inGameHud == null)
            return fallback;

        try
        {
            final DebugHudAccessor hud = ((DebugHudAccessor) ((InGameHudAccessor) minecraft.inGameHud).getDebugHud());
            return hud.getLeftText();
        }
        catch (Exception ignored)
        {
            return fallback;
        }
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
    public CommandRegistrar commandRegistrar()
    {
        return commandRegistrar;
    }
}
