package me.videogamesm12.w2k.kernel.driver.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WDriverMetadata
{
    /**
     * Unique identifier for this driver. Not used for anything currently, but it might in the future.
     * @return String
     */
    String identifier();

    /**
     * Mod IDs for mods that are required for the driver to be enabled
     * @return A list of strings
     */
    String[] requiredMods() default {};

    /**
     * Mod IDs for mods that can't be present for the driver to be enabled
     * @return A list of strings
     */
    String[] breaks() default {};
}
