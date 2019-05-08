package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GlyphRenderer
{
    private final Identifier id;
    private final float uMin;
    private final float uMax;
    private final float vMin;
    private final float vMax;
    private final float xMin;
    private final float xMax;
    private final float yMin;
    private final float yMax;
    
    public GlyphRenderer(final Identifier identifier, final float uMin, final float uMax, final float vMin, final float vMax, final float xMin, final float xMax, final float yMin, final float yMax) {
        this.id = identifier;
        this.uMin = uMin;
        this.uMax = uMax;
        this.vMin = vMin;
        this.vMax = vMax;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }
    
    public void draw(final TextureManager textureManager, final boolean italic, final float x, final float y, final BufferBuilder buffer, final float red, final float green, final float blue, final float alpha) {
        final int integer10 = 3;
        final float float11 = x + this.xMin;
        final float float12 = x + this.xMax;
        final float float13 = this.yMin - 3.0f;
        final float float14 = this.yMax - 3.0f;
        final float float15 = y + float13;
        final float float16 = y + float14;
        final float float17 = italic ? (1.0f - 0.25f * float13) : 0.0f;
        final float float18 = italic ? (1.0f - 0.25f * float14) : 0.0f;
        buffer.vertex(float11 + float17, float15, 0.0).texture(this.uMin, this.vMin).color(red, green, blue, alpha).next();
        buffer.vertex(float11 + float18, float16, 0.0).texture(this.uMin, this.vMax).color(red, green, blue, alpha).next();
        buffer.vertex(float12 + float18, float16, 0.0).texture(this.uMax, this.vMax).color(red, green, blue, alpha).next();
        buffer.vertex(float12 + float17, float15, 0.0).texture(this.uMax, this.vMin).color(red, green, blue, alpha).next();
    }
    
    @Nullable
    public Identifier getId() {
        return this.id;
    }
}
