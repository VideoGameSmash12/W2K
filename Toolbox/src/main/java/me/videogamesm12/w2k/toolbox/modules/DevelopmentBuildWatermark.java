package me.videogamesm12.w2k.toolbox.modules;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DevelopmentBuildWatermark extends WModule
{
    @Getter
    private static final BuildMetadata meta = BuildMetadata.getMetadataFromMod("w2k");

    public DevelopmentBuildWatermark()
    {
        super("Watermark", "Displays a watermark when you are using a build with uncommitted changes.");
    }

    @Override
    public boolean isEnabled()
    {
        return super.isEnabled() || meta.isDirty();
    }

    @Override
    public void setEnabled(boolean value)
    {
        if (meta.isDirty())
            throw new UnsupportedOperationException("Builds with uncommitted changes cannot have their watermark disabled.");

        super.setEnabled(value);
    }

    public static List<Component> createWatermarkText()
    {
        return Arrays.asList(
                Component.text("W2K" + (Objects.requireNonNull(meta).isDirty() ? " Development" : "") + " Build " + (meta.isDirty() ? meta.getCompileDateFormatted() : meta.getBuildNumber())).decorate(TextDecoration.BOLD),
                Component.text("For more information about this build, use /w2k details.", NamedTextColor.GRAY));
    }
}
