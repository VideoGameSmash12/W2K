package me.videogamesm12.w2k.integrator.core;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntegratorMetadata
{
    String[] required() default {};

    String[] requiredClasses() default {};

    String[] breaks() default {};
}
