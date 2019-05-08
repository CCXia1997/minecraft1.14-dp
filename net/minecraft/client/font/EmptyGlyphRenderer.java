package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmptyGlyphRenderer extends GlyphRenderer
{
    public EmptyGlyphRenderer() {
        super(new Identifier(""), 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void draw(final TextureManager textureManager, final boolean italic, final float x, final float y, final BufferBuilder buffer, final float red, final float green, final float blue, final float alpha) {
    }
    
    @Nullable
    @Override
    public Identifier getId() {
        return null;
    }
}
