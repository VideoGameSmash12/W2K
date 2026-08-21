/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.w2k.supervisor.components.fantasia;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.videogamesm12.w2k.supervisor.components.fantasia.listener.IConnectionListener;
import me.videogamesm12.w2k.supervisor.components.fantasia.listener.TelnetConnectionListener;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1>ConnectionType</h1>
 * <p>An enum that stores the various types of methods to connect to the Fantasia server.</p>
 */
@RequiredArgsConstructor
public class ConnectionType<T extends IConnectionListener>
{
    private static final Map<String, ConnectionType<?>> registry = new HashMap<>();
    //--
    public static final ConnectionType<TelnetConnectionListener> TELNET = register(new ConnectionType<>("w2k:telnet", TelnetConnectionListener.class));
    //--
    static
    {
        // Search for available connection types for future use
        FabricLoader.getInstance().getEntrypoints("w2k-supervisor-connection-type", ConnectionType.class)
                .forEach(ConnectionType::register);
    }
    //--
    @Getter
    private final String key;
    @Getter
    private final Constructor<T> constructor;

    public ConnectionType(final String key, final Class<T> clazz)
    {
        this.key = key;

        try
        {
            this.constructor = clazz.getConstructor(Server.class);
        }
        catch (NoSuchMethodException ex)
        {
            throw new IllegalArgumentException(clazz.getName() + " does not have a constructor that only takes class " + Server.class.getName());
        }
    }

    public T createListener(Server arguments) throws InvocationTargetException, InstantiationException, IllegalAccessException
    {
        return constructor.newInstance(arguments);
    }

    @Override
    public String toString()
    {
        return key;
    }

    public static <T extends IConnectionListener> ConnectionType<T> get(String key)
    {
        // Legacy keys from when the connection types were an enum
        if (!key.contains(":"))
        {
            key = "w2k:" + key.toLowerCase();
        }

        // Make sure it's an actual valid connection type
        if (!registry.containsKey(key))
        {
            throw new IllegalArgumentException(key + " is not a registered connection type");
        }

        return (ConnectionType<T>) registry.get(key);
    }

    private static <T extends IConnectionListener> ConnectionType<T> register(ConnectionType<T> type)
    {
        registry.put(type.key, type);
        return type;
    }
}