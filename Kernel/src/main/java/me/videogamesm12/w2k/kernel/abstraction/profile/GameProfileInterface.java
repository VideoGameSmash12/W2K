package me.videogamesm12.w2k.kernel.abstraction.profile;

import com.google.common.collect.Multimap;
import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

import java.util.UUID;

/**
 * <h1>GameProfileInterface</h1>
 * <p>A wrapper interface for {@link com.mojang.authlib.GameProfile} instances implemented using Mixins.
 */
public interface GameProfileInterface extends ObjectInterface
{
    /**
     * Gets the username associated with this GameProfile.
     * @return  {@link String}
     */
    String w2k$name();

    /**
     * Gets the UUID associated with this GameProfile.
     * @return  {@link UUID}
     */
    UUID w2k$uuid();

    /**
     * Gets the GameProfile's properties.
     * @return  {@link Multimap}
     */
    Multimap<String, PropertyInterface> w2k$properties();
}
