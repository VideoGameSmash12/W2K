package me.videogamesm12.w2k.kernel.data;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TextOverlay extends Overlay
{
    private final Supplier<Object> markerGetter;
    private final Supplier<List<Component>> contentSupplier;
    @Getter
    private final boolean shadowEnabled;

    private Object marker;
    private List<Object> compiled;

    public TextOverlay(final int x,
                       final int y,
                       final Alignment horizontalAlignment,
                       final Alignment verticalAlignment,
                       final Predicate<Overlay> shouldDisplay,
                       final Supplier<List<Component>> contentSupplier,
                       final Supplier<Object> markerGetter,
                       final boolean shadowEnabled)
    {
        super("w2k:text", x, y, horizontalAlignment, verticalAlignment, shouldDisplay);
        this.contentSupplier = contentSupplier;
        this.markerGetter = markerGetter;
        this.shadowEnabled = shadowEnabled;
        update();
    }

    // compiles and updates based on the content supplier
    public void update()
    {
        compiled = contentSupplier.get().stream().map(content -> val().text().adventureToNative(content)).collect(Collectors.toList());
        this.marker = markerGetter.get();
    }

    public boolean shouldUpdate()
    {
        return getShouldDisplay().test(this) && !this.marker.equals(markerGetter.get());
    }

    public <Text> List<Text> getCompiledText()
    {
        return (List<Text>) compiled;
    }
}
