package me.videogamesm12.w2k.val.v1_20_1;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.w2k.kernel.abstraction.conversion.NBTConverter;
import me.videogamesm12.w2k.kernel.abstraction.conversion.TextComponentConverter;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.render.OverlayRenderDispatcher;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.data.TextOverlay;
import me.videogamesm12.w2k.kernel.data.Overlay;
import me.videogamesm12.w2k.kernel.event.entity.EntityInteractionEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStartedEvent;
import me.videogamesm12.w2k.kernel.event.lifecycle.ClientStoppedEvent;
import me.videogamesm12.w2k.kernel.event.network.DisconnectEvent;
import me.videogamesm12.w2k.kernel.event.network.JoinEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import me.videogamesm12.w2k.val.v1_20_1.graphics.OverlayRenderDispatcherImpl;
import me.videogamesm12.w2k.val.v1_20_1.graphics.renderer.AbstractOverlayRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class VersionAbstractionLayer extends BaseVersionAbstractionLayer<MinecraftClient>
{
    private final TextComponentConverter<Text> textComponentConverter = new TextComponentConverter<>(
            nativeComponent -> ComponentUtils.deserializeComponent(Text.Serializer.toJsonTree(nativeComponent)),
            adventureComponent -> Text.Serializer.fromJson(ComponentUtils.serializeComponent(adventureComponent)),
            adventureComponent -> Objects.requireNonNull(Text.Serializer.fromJson(ComponentUtils.serializeComponent(adventureComponent))).getString()
    );
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
    private final OverlayRenderDispatcherImpl renderDispatcher;

    public VersionAbstractionLayer()
    {
        super(MinecraftClient.getInstance());
        this.renderDispatcher = new OverlayRenderDispatcherImpl();
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
    public Optional<ClientPlayerEntity> getLocalPlayer()
    {
        return Optional.ofNullable(minecraft.player);
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
    public TextComponentConverter<Text> text()
    {
        return textComponentConverter;
    }

    @Override
    public NBTConverter<NbtCompound> nbt()
    {
        return nbtConverter;
    }

    @Override
    public OverlayRenderDispatcher<AbstractOverlayRenderer> renderDispatcher()
    {
        return renderDispatcher;
    }
}
