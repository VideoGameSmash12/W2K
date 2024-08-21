package me.videogamesm12.w2k.kernel.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Parameters
{
    /**
     * The command's name.
     * @return  String
     */
    String name();

    /**
     * The command's usage.
     * @return  String
     */
    String usage();
}
