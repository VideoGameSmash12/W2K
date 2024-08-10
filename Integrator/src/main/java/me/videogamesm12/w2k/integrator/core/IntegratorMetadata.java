package me.videogamesm12.w2k.integrator.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntegratorMetadata
{
    String[] required();

    String[] breaks() default {};
}
