package me.videogamesm12.w2k.val.v1_20_1;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.abstraction.BaseVersionAbstractionLayer;
import me.videogamesm12.w2k.kernel.abstraction.conversion.NBTConverter;
import me.videogamesm12.w2k.kernel.abstraction.conversion.TextComponentConverter;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
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
    private final Map<String, BiConsumer<Overlay, DrawContext>> overlayRendererRegistry = new HashMap<>();

    public VersionAbstractionLayer()
    {
        super(MinecraftClient.getInstance());
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

        // Register
        overlayRendererRegistry.put("w2k:text", (overlay, context) ->
        {
            final TextOverlay textOverlay = (TextOverlay) overlay;

            final Overlay.Alignment horizontal = overlay.getHorizontalAlignment();
            final Overlay.Alignment vertical = overlay.getVerticalAlignment();

            // Get the starting position
            int xi = switch (horizontal)
            {
                case LEAST -> 0;
                case CENTER -> context.getScaledWindowWidth() / 2;
                case MOST -> context.getScaledWindowWidth();
            };
            int y = switch (vertical)
            {
                case LEAST -> 0;
                case CENTER -> context.getScaledWindowHeight() / 2;
                case MOST -> context.getScaledWindowHeight();
            };

            if (textOverlay.shouldUpdate())
            {
                textOverlay.update();
            }

            final List<Text> compiled = textOverlay.getCompiledText();

            for (int i = 0; i < compiled.size(); i++)
            {
                Text text = compiled.get(i);
                int level = (i * minecraft.textRenderer.fontHeight);

                int x = switch (horizontal)
                {
                    case LEAST -> overlay.getX();
                    case CENTER -> xi - horizontal.offset(minecraft.textRenderer.getWidth(text));
                    case MOST -> (xi - minecraft.textRenderer.getWidth(text)) + horizontal.offset(overlay.getX());
                };
                y = switch (vertical)
                {
                    case LEAST -> overlay.getY() + level;
                    case CENTER -> y + (overlay.getY() + level);
                    case MOST -> (y - overlay.getY() - level);
                };

                context.drawText(minecraft.textRenderer, text, x, y, 0xFFFFFF, textOverlay.isShadowEnabled());

            }
        });

        // Register our overlay renderer
        HudRenderCallback.EVENT.register((lol, ass) ->
        {
            W2K.getInstance().getModuleManager().getIdRegistry().values().stream()
                    .filter(WModule::isEnabled)
                    .map(WModule::getOverlays)
                    .flatMap(Collection::stream)
                    .filter(overlay -> overlay.getShouldDisplay().test(overlay))
                    .forEach(overlay ->
                    {
                        overlayRendererRegistry.getOrDefault(overlay.getId(), (ignored1, ignored2) -> {})
                                .accept(overlay, lol);
                    });
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
}
