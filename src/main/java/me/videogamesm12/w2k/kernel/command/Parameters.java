package me.videogamesm12.w2k.kernel.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters
{
    String name();
    String usage();
}
