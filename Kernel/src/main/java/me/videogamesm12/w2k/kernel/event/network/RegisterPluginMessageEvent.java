package me.videogamesm12.w2k.kernel.event.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.kernel.event.CustomEvent;

/**
 * <h1>RegisterPluginMessageEvent</h1>
 * <p>Event that is called when the client gets the chance to register plugin messages (also known as custom
 *  payloads).</p>
 */
@Getter
@RequiredArgsConstructor
public class RegisterPluginMessageEvent extends CustomEvent
{
    private final Object client;
}
