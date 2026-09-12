package me.videogamesm12.w2k.toolbox.modules;

import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.BuildMetadata;
import me.videogamesm12.w2k.kernel.module.WModule;

public class DevelopmentBuildWatermark extends WModule
{
    @Getter
    private static final BuildMetadata meta = BuildMetadata.getMetadataFromClassJar(W2K.class);

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
}
