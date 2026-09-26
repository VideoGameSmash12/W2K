package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.event.render.OverlayRequestEvent;
import me.videogamesm12.w2k.kernel.graphics.Alignment;
import me.videogamesm12.w2k.kernel.graphics.core.TextLabel;
import me.videogamesm12.w2k.kernel.module.WModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

public class Watermark extends WModule
{
    @Getter
    private static final BuildMetadata meta = BuildMetadata.getMetadataFromMod("w2k");

    public Watermark()
    {
        super("Watermark", "Displays a watermark containing details about the build you are running");

        addDrawableOverlay(new TextLabel(Component.text("W2K " + (Objects.requireNonNull(meta).isDirty() ? "Development " : "") + "Build " + (meta.isDirty() ? meta.getCompileDateFormatted() : meta.getBuildNumber())).decorate(TextDecoration.BOLD), 2, 2, Alignment.MOST, Alignment.LEAST));
        addDrawableOverlay(new TextLabel(Component.text("For more information about this build, use /w2k details.", NamedTextColor.GRAY), 2, 11, Alignment.MOST, Alignment.LEAST));
    }

    @Subscribe
    public void onOverlayRequest(OverlayRequestEvent event)
    {
        event.submitAll(getDrawableOverlays());
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
}
