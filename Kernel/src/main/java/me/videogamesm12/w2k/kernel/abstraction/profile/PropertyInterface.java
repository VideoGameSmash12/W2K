package me.videogamesm12.w2k.kernel.abstraction.profile;

import me.videogamesm12.w2k.kernel.abstraction.ObjectInterface;

/**
 * <h1>PropertyInterface</h1>
 * <p>A wrapper interface for {@link com.mojang.authlib.properties.Property} instances implemented using Mixins.
 */
public interface PropertyInterface extends ObjectInterface
{
    /**
     * Gets the property's name.
     * @return  {@link String}
     */
    String w2k$name();

    /**
     * Gets the property's signature.
     * @return  {@link String}
     */
    String w2k$value();

    /**
     * Gets the property's signature.
     * @return  {@link String}
     */
    String w2k$signature();
}
