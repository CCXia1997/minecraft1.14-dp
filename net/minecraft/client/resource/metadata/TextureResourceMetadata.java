package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureResourceMetadata
{
    public static final TextureResourceMetadataReader READER;
    private final boolean blur;
    private final boolean clamp;
    
    public TextureResourceMetadata(final boolean blur, final boolean boolean2) {
        this.blur = blur;
        this.clamp = boolean2;
    }
    
    public boolean shouldBlur() {
        return this.blur;
    }
    
    public boolean shouldClamp() {
        return this.clamp;
    }
    
    static {
        READER = new TextureResourceMetadataReader();
    }
}
