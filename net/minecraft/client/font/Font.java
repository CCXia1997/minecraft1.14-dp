package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.Closeable;

@Environment(EnvType.CLIENT)
public interface Font extends Closeable
{
    default void close() {
    }
    
    @Nullable
    default RenderableGlyph getGlyph(final char character) {
        return null;
    }
}
