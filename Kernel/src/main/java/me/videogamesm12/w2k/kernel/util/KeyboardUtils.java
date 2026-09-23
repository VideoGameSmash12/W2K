package me.videogamesm12.w2k.kernel.util;

import me.videogamesm12.w2k.kernel.W2K;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KeyboardUtils
{
    private static final Map<String, Integer> modifierMap = new HashMap<>();
    private static final Map<String, Integer> keyMap = new HashMap<>();

    static
    {
        // LWJGL v3 (1.13 - 26.2)
        if (VersionUtils.isNewerThanOrRunning("1.13") && VersionUtils.isOlderThanOrRunning("26.2"))
        {
            // This is a pretty bad solution and will most likely introduce compatibility issues later down the line,
            //  but
            try
            {
                final Field[] glfwFields = Class.forName("org.lwjgl.glfw.GLFW").getFields();

                // Scan for modifiers
                Arrays.stream(glfwFields)
                        .filter(field -> field.getType().equals(int.class))
                        .filter(field -> field.getName().startsWith("GLFW_MOD_"))
                        .forEach(field ->
                        {
                            try
                            {
                                modifierMap.put("modifier." + field.getName()
                                                .replace("GLFW_MOD_", "")
                                                .replace("_", ".")
                                                .toLowerCase(),
                                        field.getInt(null));
                            }
                            catch (IllegalAccessException ex)
                            {
                                W2K.getLogger().error("Unable to resolve modifier from field {}", field.getName(), ex);
                            }
                        });

                // Scan for keys
                Arrays.stream(glfwFields)
                        .filter(field -> field.getType().equals(int.class))
                        .filter(field -> field.getName().startsWith("GLFW_KEY_"))
                        .forEach(field ->
                        {
                            try
                            {
                                keyMap.put("key.keyboard." + field.getName()
                                                .replace("GLFW_KEY_", "")
                                                .replace("_", ".")
                                                .toLowerCase(),
                                        field.getInt(null));
                            }
                            catch (IllegalAccessException ex)
                            {
                                W2K.getLogger().error("Unable to resolve key from field {}", field.getName(), ex);
                            }
                        });
            }
            catch (ClassNotFoundException ex)
            {
                throw new RuntimeException("Missing LWJGL v3! Are you using a custom rendering library? If so, please create an issue on W2K's GitHub to add support for this!", ex);
            }
        }
        // Blaze3D (26.3+)
        else if (VersionUtils.isNewerThanOrRunning("26.3"))
        {
            // Will be added when we add support
        }
    }

    public static Integer getKeyId(final String... ids)
    {
        for (String candidate : ids)
        {
            if (keyMap.containsKey(candidate))
            {
                return keyMap.get(candidate);
            }
        }

        return null;
    }

    public static Integer getModifier(final String... ids)
    {
        for (String candidate : ids)
        {
            if (modifierMap.containsKey(candidate))
            {
                return modifierMap.get(candidate);
            }
        }

        return null;
    }
}
