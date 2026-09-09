package me.videogamesm12.w2k.kernel.data;

import lombok.Builder;
import lombok.Data;
import me.videogamesm12.w2k.kernel.W2K;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.io.File;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * <h1>BuildMetadata</h1>
 * <p>Wrapper for git.properties, a file included in builds of W2K.</p>
 */
@Data
@Builder
public class BuildMetadata
{
    private static final Map<Class<?>, BuildMetadata> cache = new HashMap<>();

    private final String branch;
    private final String commitId;
    private final String commitIdAbbreviated;
    private final String commitTime;
    private final String originUrl;
    private final boolean dirty;
    private final long compileDate;
    private final String compileDateFormatted;

    @Override
    public String toString()
    {
        return "--=== BUILD INFO ===--" + "\r\n"
                + "Branch: " + branch + "\r\n"
                + "Commit: " + commitId + " (" + commitIdAbbreviated + ")\r\n"
                + "Commit Date: " + commitTime + "\r\n"
                + "Origin URL: " + originUrl + "\r\n"
                + "Dirty: " + dirty + "\r\n"
                + "Compile Date: " + compileDateFormatted + " (" + compileDate + ")";
    }

    public String toCrashReportSection()
    {
        return "\tBranch: " + branch + "\r\n"
                + "\tCommit: " + commitId + " (" + commitIdAbbreviated + ")\r\n"
                + "\tCommit Date: " + commitTime + "\r\n"
                + "\tOrigin URL: " + originUrl + "\r\n"
                + "\tDirty: " + dirty + "\r\n"
                + "\tCompile Date: " + compileDateFormatted + " (" + compileDate + ")";
    }

    public Component toComponent()
    {
        return Component.translatable("%s", Component.translatable("w2k.command.w2k.build_info.header").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)).color(NamedTextColor.GRAY) // $#&^!
                .append(Component.newline())
                .append(Component.translatable("w2k.command.w2k.build_info.branch", Component.text(branch).color(NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(Component.translatable("w2k.command.w2k.build_info.commit_id",
                        Component.text(commitId).color(NamedTextColor.WHITE),
                        Component.text(commitIdAbbreviated).color(NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(Component.translatable("w2k.command.w2k.build_info.commit_time",
                        Component.text(commitTime).color(NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(Component.translatable("w2k.command.w2k.build_info.origin_url",
                        Component.text(originUrl).color(NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(Component.translatable("w2k.command.w2k.build_info.dirty",
                        Component.text(dirty).color(NamedTextColor.WHITE)))
                .hoverEvent(HoverEvent.showText(Component.translatable("chat.click.copy_to_clipboard")))
                .clickEvent(ClickEvent.copyToClipboard(toString()));
    }

    public static BuildMetadata getMetadataFromClassJar(Class<?> modClass)
    {
        if (!cache.containsKey(modClass))
        {
            // In a perfect world, git.properties would instead be formatted as JSON and readable with GSON. That is not the
            //  case due to plugin shenanigans
            final Properties properties = new Properties();
            try (final ZipFile zip = new ZipFile(new File(modClass.getProtectionDomain().getCodeSource().getLocation().toURI())))
            {
                properties.load(zip.getInputStream(zip.getEntry("git.properties")));

                cache.put(modClass, BuildMetadata.builder()
                        .branch(properties.getProperty("git.branch"))
                        .commitId(properties.getProperty("git.commit.id"))
                        .commitIdAbbreviated(properties.getProperty("git.commit.id.abbrev"))
                        .commitTime(properties.getProperty("git.commit.time"))
                        .originUrl(properties.getProperty("git.remote.origin.url"))
                        .dirty(Boolean.parseBoolean(properties.getProperty("git.dirty")))
                        .compileDate(Long.parseLong(properties.getProperty("w2k.build.timestamp")))
                        .compileDateFormatted(properties.getProperty("w2k.build.timestamp.formatted"))
                        .build());
            }
            catch (Throwable ex)
            {
                W2K.getLogger().error("Failed to read JAR build properties", ex);
                return null;
            }
        }

        return cache.get(modClass);
    }
}
