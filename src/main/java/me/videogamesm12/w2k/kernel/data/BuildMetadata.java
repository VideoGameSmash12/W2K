package me.videogamesm12.w2k.kernel.data;

import lombok.Builder;
import lombok.Data;
import me.videogamesm12.w2k.kernel.W2K;

import java.util.Properties;
import java.util.zip.ZipFile;

/**
 * <h1>BuildMetadata</h1>
 * <p>Wrapper for git.properties, a file included in builds of W2K.</p>
 */
@Data
@Builder
public class BuildMetadata
{
    private final String branch;
    private final String commitId;
    private final String commitIdAbbreviated;
    private final String commitTime;
    private final String originUrl;
    private final boolean dirty;

    @Override
    public String toString()
    {
        return "--=== BUILD INFO ===--" + "\r\n"
                + "Branch: " + branch + "\r\n"
                + "Commit: " + commitId + " (" + commitIdAbbreviated + ")\r\n"
                + "Commit Date: " + commitTime + "\r\n"
                + "Origin URL: " + originUrl + "\r\n"
                + "Dirty: " + dirty;
    }

    public String toCrashReportSection()
    {
        return "\tBranch: " + branch + "\r\n"
                + "\tCommit: " + commitId + " (" + commitIdAbbreviated + ")\r\n"
                + "\tCommit Date: " + commitTime + "\r\n"
                + "\tOrigin URL: " + originUrl + "\r\n"
                + "\tDirty: " + dirty;
    }

    public static BuildMetadata getMetadataFromClassJar(Class<?> modClass)
    {
        // In a perfect world, git.properties would instead be formatted as JSON and readable with GSON. That is not the
        //  case due to plugin shenanigans
        final Properties properties = new Properties();
        try
        {
            properties.load(modClass.getClassLoader().getResourceAsStream("git.properties"));

            return BuildMetadata.builder()
                    .branch(properties.getProperty("git.branch"))
                    .commitId(properties.getProperty("git.commit.id"))
                    .commitIdAbbreviated(properties.getProperty("git.commit.id.abbrev"))
                    .commitTime(properties.getProperty("git.commit.time"))
                    .originUrl(properties.getProperty("git.remote.origin.url"))
                    .dirty(Boolean.parseBoolean(properties.getProperty("git.dirty")))
                    .build();
        }
        catch (Throwable ex)
        {
            W2K.getLogger().error("Failed to read JAR build properties", ex);

            return null;
        }
    }

    public static BuildMetadata getMetadataFromZipFile(ZipFile file)
    {
        // In a perfect world, git.properties would instead be formatted as JSON and readable with GSON. That is not the
        //  case due to plugin shenanigans
        final Properties properties = new Properties();
        try
        {
            properties.load(file.getInputStream(file.getEntry("git.properties")));

            return BuildMetadata.builder()
                    .branch(properties.getProperty("git.branch"))
                    .commitId(properties.getProperty("git.commit.id"))
                    .commitIdAbbreviated(properties.getProperty("git.commit.id.abbrev"))
                    .commitTime(properties.getProperty("git.commit.time"))
                    .originUrl(properties.getProperty("git.remote.origin.url"))
                    .dirty(Boolean.parseBoolean(properties.getProperty("git.dirty")))
                    .build();
        }
        catch (Throwable ex)
        {
            W2K.getLogger().error("Failed to read JAR build properties", ex);

            return null;
        }
    }
}
