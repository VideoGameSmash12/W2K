package me.videogamesm12.w2k.kernel.driver.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WDriverMetadata
{
    String identifier();

    int minProtocolVersion();

    int maxProtocolVersion();

    String minVersion();

    String maxVersion();

    String[] requiredMods() default {};
}
