package me.videogamesm12.w2k.kernel.abstraction;

import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.abstraction.command.AbstractCommandRegistrar;
import me.videogamesm12.w2k.kernel.abstraction.conversion.NBTConverter;
import me.videogamesm12.w2k.kernel.abstraction.conversion.TextComponentConverter;
import me.videogamesm12.w2k.kernel.abstraction.network.PlayNetworkHandlerInterface;
import me.videogamesm12.w2k.kernel.abstraction.render.OverlayRenderDispatcher;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientWorldInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.command.AbstractArgumentResolver;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;

/**
 * <h1>BaseVersionAbstractionLayer</h1>
 * <p>An abstract class used to keep version specific code separate from the codebases of W2K's components.</p>
 * <p>Subprojects containing Version Abstraction Layers serve two purposes:</p>
 * <ol>
 *     <li>Implement the {@link ObjectInterface} variations located in {@code me.videogamesm12.w2k.kernel}. The
 *     implementations should try to remain functionally consistent where possible and stay organized. For example,
 *     interfaces related to client-server communication (such as the network handler) should be in a package separate
 *     from interface where objects you'd see in a world would be located.</li>
 *     <li>Forward events from the mod loader's API to W2K's own {@link me.videogamesm12.w2k.kernel.event.CustomEvent}
 *     system and inject code into the game to call our own events if no such events exist</li>
 * </ol>
 * <p>Code changes on this level should only be done to ensure that the purposes mentioned above are accomplished where
 *  possible.</p>
 * @param <Minecraft>   MinecraftClient or Minecraft (depending on the mappings you are using)
 */
@RequiredArgsConstructor
public abstract class BaseVersionAbstractionLayer<Minecraft>
{
    protected final Minecraft minecraft;

    /**
     * Performs additional operations like registering API specific event listeners or registering commands.
     */
    public abstract void setup();

    /**
     * <p>Gets the {@code ClientPlayNetworkHandler} for an active connection to a server.</p>
     * <p>Minecraft does not re-use instances of these across server sessions and will only have one present during an
     *  active connection to a server. As such, this is an optional to allow you to perform actions only if a handler
     *  is present using methods like {@code ifPresent}.</p>
     * @return  {@link Optional<PlayNetworkHandlerInterface>}
     */
    public abstract Optional<PlayNetworkHandlerInterface> networkHandler();

    /**
     * <p>Gets the {@code ClientPlayerEntity} representing the player.</p>
     * <p>Like {@code ClientPlayNetworkHandler}s, Minecraft does not re-use instances of these across server or game
     *  sessions and will only have one present when in-game. As such, this is an optional to allow you to perform
     *  actions only if the player is in-game.</p>
     * @return          {@link Optional<ClientPlayerEntityInterface>}
     * @param <Player>  {@code ClientPlayerEntity} or {@code LocalPlayer} (depending on your mappings)
     */
    public abstract <Player extends ClientPlayerEntityInterface> Optional<Player> getLocalPlayer();

    /**
     * <p>Gets the {@code ClientWorld} representing the client-side world.</p>
     * <p>This is an optional to allow you to perform actions only if the player is in-game.</p>
     * @return              {@link Optional<ClientWorld>}
     * @param <ClientWorld> {@code ClientWorld} or {@code ClientLevel} (depending on your mappings)
     */
    public abstract <ClientWorld extends ClientWorldInterface> Optional<ClientWorld> getLocalWorld();

    /**
     * <p>Gets the {@code Entity} representing the player that the user is currently looking at.</p>
     * <p>Since the player is not always going to be looking at an entity, this is an optional to allow you to perform
     *  actions only if the user is looking at an entity.</p>
     * @return          Entity
     * @param <Entity>  {@code Entity}
     */
    public abstract <Entity extends EntityInterface> Optional<Entity> getTargetedEntity();

    /**
     * <p>Gets the {@code Entity} representing the player that the user is currently looking at.</p>
     * <p>Since the player is not always going to be looking at an entity, this can have a chance of being null.</p>
     * @return          Entity
     */
    public abstract EntityInterface getTargetedEntityUnsafe();

    /**
     * Instructs the client to close whatever screen is open.
     */
    public abstract void closeCurrentScreen();

    /**
     * Executes the given task on Minecraft's main thread.
     * @param runnable  Runnable
     */
    public abstract void execute(Runnable runnable);

    /**
     * Gets a {@link TextComponentConverter} instance which converts text components between Minecraft's native text
     *  component and Adventure's text component systems.
     * @return          {@link TextComponentConverter}
     * @param <Text>    {@code Text} or {@code Component} (depends on your mappings)
     */
    public abstract <Text> TextComponentConverter<Text> text();

    /**
     * Gets an {@link NBTConverter} instance which converts NBT objects between Minecraft's native NBT objects and
     *  Adventure's NBT objects.
     * @return              {@link NBTConverter}
     * @param <NbtCompound> {@code NbtCompound} or {@code CompoundTag} (depends on your mappings)
     */
    public abstract <NbtCompound> NBTConverter<NbtCompound> nbt();

    public abstract <BaseRenderer> OverlayRenderDispatcher<BaseRenderer> renderDispatcher();

    public abstract <Resolver extends AbstractArgumentResolver<?>> AbstractCommandRegistrar<Resolver> commandRegistrar();

    /**
     * <p>Gets the current client version using the Fabric Loader API.</p>
     * @return  {@link String}
     */
    public String getVersion()
    {
        return FabricLoader.getInstance().getModContainer("minecraft")
                .orElseThrow(() -> new IllegalStateException("Minecraft is somehow not a mod container? What?"))
                .getMetadata()
                .getVersion()
                .getFriendlyString();
    }
}
