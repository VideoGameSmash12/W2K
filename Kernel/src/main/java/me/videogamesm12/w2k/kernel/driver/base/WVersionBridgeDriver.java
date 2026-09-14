package me.videogamesm12.w2k.kernel.driver.base;

import com.google.common.annotations.Beta;
import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.*;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>WVersionBridgeDriver</h1>
 * <p>A specific kind of driver which is used by W2K's components to call Minecraft code (which may differ between
 * versions) without relying on version specific code.</p>
 * <p>In the future, this system may be revised to be better organized so that everything isn't in one big driver.</p>
 */
public interface WVersionBridgeDriver extends WDriver
{

    /**
     * Schedules a safe client shutdown.
     */
    void scheduleSafeShutdown();

    /**
     * Gets the player's current username.
     * @return  String
     */
    String getCurrentUsername();
}
