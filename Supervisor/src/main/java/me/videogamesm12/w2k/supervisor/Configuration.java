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

package me.videogamesm12.w2k.supervisor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.configuration.SerializableSection;
import me.videogamesm12.w2k.supervisor.components.fantasia.ConnectionType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

@Builder
@Getter
public class Configuration implements SerializableSection
{
    private final Fantasia fantasiaSettings;

    private final Network networkSettings;

    private Rendering renderingSettings;

    private Watchdog watchdogSettings;

    @Override
    public CompoundBinaryTag toNbt()
    {
        return CompoundBinaryTag.builder()
                .put("fantasia", fantasiaSettings.toNbt())
                .put("network", networkSettings.toNbt())
                .put("rendering", renderingSettings.toNbt())
                .put("watchdog", watchdogSettings.toNbt())
                .build();
    }

    public static Configuration fromNbt(CompoundBinaryTag tag)
    {
        return builder()
                .fantasiaSettings(Fantasia.fromNbt(tag.getCompound("fantasia", CompoundBinaryTag.empty())))
                .networkSettings(Network.fromNbt(tag.getCompound("network", CompoundBinaryTag.empty())))
                .renderingSettings(Rendering.fromNbt(tag.getCompound("rendering", CompoundBinaryTag.empty())))
                .watchdogSettings(Watchdog.fromNbt(tag.getCompound("watchdog", CompoundBinaryTag.empty())))
                .build();
    }

    @Builder
    @Getter
    @Setter
    public static class Fantasia implements SerializableSection
    {
        @Builder.Default
        private int port = 6969;

        @Builder.Default
        private String connectionType = ConnectionType.TELNET.getKey();

        @Builder.Default
        private boolean nonLocalConnectionsAllowed = false;

        public Map<String, Object> getSettings()
        {
            final Map<String, Object> map = new WeakHashMap<>();
            Arrays.stream(getClass().getDeclaredFields()).forEach(field ->
            {
                try
                {
                    map.put(field.getName(), field.get(this));
                }
                catch (IllegalAccessException ignored)
                {
                }
            });

            return map;
        }

        @Override
        public CompoundBinaryTag toNbt()
        {
            final CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();
            builder.putInt("port", port);
            builder.putString("connection_type", connectionType);
            builder.putBoolean("local_only", nonLocalConnectionsAllowed);
            return builder.build();
        }

        public static Fantasia fromNbt(CompoundBinaryTag tag)
        {
            return builder()
                    .port(tag.getInt("port", 6969))
                    .connectionType(tag.getString("connection_type", ConnectionType.TELNET.getKey()))
                    .nonLocalConnectionsAllowed(tag.getBoolean("local_only", false))
                    .build();
        }
    }

    @Builder
    @Getter
    @Setter
    public static class Network implements SerializableSection
    {
        private boolean ignoringEntitySpawns;

        private boolean ignoringExplosions;

        private boolean ignoringLightUpdates;

        private boolean ignoringParticleSpawns;

        private boolean ignoringMapUpdates;

        private boolean ignoringScreens;

        public Map<String, Boolean> getSettings()
        {
            final Map<String, Boolean> map = new WeakHashMap<>();
            Arrays.stream(getClass().getDeclaredFields()).forEach(field ->
            {
                try
                {
                    map.put(field.getName(), field.getBoolean(this));
                }
                catch (IllegalAccessException ignored)
                {
                }
            });

            return map;
        }

        @Override
        public CompoundBinaryTag toNbt()
        {
            return CompoundBinaryTag.builder()
                    .putBoolean("ignore_entity_spawns", ignoringEntitySpawns)
                    .putBoolean("ignore_explosions", ignoringExplosions)
                    .putBoolean("ignore_light_updates", ignoringLightUpdates)
                    .putBoolean("ignore_particle_spawns", ignoringParticleSpawns)
                    .putBoolean("ignore_map_updates", ignoringMapUpdates)
                    .putBoolean("ignore_screens", ignoringScreens)
                    .build();
        }

        public static Network fromNbt(CompoundBinaryTag tag)
        {
            return builder()
                    .ignoringEntitySpawns(tag.getBoolean("ignore_entity_spawns"))
                    .ignoringExplosions(tag.getBoolean("ignore_explosions"))
                    .ignoringLightUpdates(tag.getBoolean("ignore_light_updates"))
                    .ignoringParticleSpawns(tag.getBoolean("ignore_particle_spawns"))
                    .ignoringMapUpdates(tag.getBoolean("ignore_map_updates"))
                    .ignoringScreens(tag.getBoolean("ignore_screens"))
                    .build();
        }
    }

    @Builder
    @Getter
    @Setter
    public static class Rendering implements SerializableSection
    {
        private boolean entityRenderingDisabled;

        private boolean gameRenderingDisabled;

        private boolean tileEntityRenderingDisabled;

        private boolean weatherRenderingDisabled;

        private boolean worldRenderingDisabled;

        public Map<String, Boolean> getSettings()
        {
            final Map<String, Boolean> map = new WeakHashMap<>();
            Arrays.stream(getClass().getDeclaredFields()).forEach(field ->
            {
                try
                {
                    map.put(field.getName(), field.getBoolean(this));
                }
                catch (IllegalAccessException ignored)
                {
                }
            });

            return map;
        }

        @Override
        public CompoundBinaryTag toNbt()
        {
            return CompoundBinaryTag.builder()
                    .putBoolean("disable_entity_rendering", entityRenderingDisabled)
                    .putBoolean("disable_game_rendering", gameRenderingDisabled)
                    .putBoolean("disable_tile_entity_rendering", tileEntityRenderingDisabled)
                    .putBoolean("disable_weather_rendering", weatherRenderingDisabled)
                    .putBoolean("disable_world_rendering", worldRenderingDisabled)
                    .build();
        }

        public static Rendering fromNbt(CompoundBinaryTag tag)
        {
            return builder()
                    .entityRenderingDisabled(tag.getBoolean("disable_entity_rendering"))
                    .gameRenderingDisabled(tag.getBoolean("disable_game_rendering"))
                    .tileEntityRenderingDisabled(tag.getBoolean("disable_tite_entity_rendering"))
                    .weatherRenderingDisabled(tag.getBoolean("disable_weather_rendering"))
                    .worldRenderingDisabled(tag.getBoolean("disable_world_rendering"))
                    .build();
        }
    }

    @Builder
    @Getter
    @Setter
    public static class Watchdog implements SerializableSection
    {
        private boolean freezeDetectionEnabled = true;

        private long freezeDetectionThreshold = 5000;

        public Map<String, Object> getSettings()
        {
            final Map<String, Object> map = new WeakHashMap<>();
            Arrays.stream(getClass().getDeclaredFields()).forEach(field ->
            {
                try
                {
                    map.put(field.getName(), field.get(this));
                }
                catch (IllegalAccessException ignored)
                {
                }
            });

            return map;
        }

        @Override
        public CompoundBinaryTag toNbt()
        {
            return CompoundBinaryTag.builder()
                    .putBoolean("detect_freezes", freezeDetectionEnabled)
                    .putLong("freeze_detection_threshold", freezeDetectionThreshold)
                    .build();
        }

        public static Watchdog fromNbt(final CompoundBinaryTag tag)
        {
            return builder()
                    .freezeDetectionEnabled(tag.getBoolean("detect_freezes", true))
                    .freezeDetectionThreshold(tag.getLong("freeze_detection_threshold", 5000))
                    .build();
        }
    }
}
