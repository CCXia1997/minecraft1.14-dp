package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.minecraft.resource.ResourceManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.Closeable;
import net.minecraft.client.texture.AbstractTexture;

@Environment(EnvType.CLIENT)
public class GlyphAtlasTexture extends AbstractTexture implements Closeable
{
    private final Identifier id;
    private final boolean hasColor;
    private final Slot rootSlot;
    
    public GlyphAtlasTexture(final Identifier identifier, final boolean hasColor) {
        this.id = identifier;
        this.hasColor = hasColor;
        this.rootSlot = new Slot(0, 0, 256, 256);
        TextureUtil.prepareImage(hasColor ? NativeImage.b.a : NativeImage.b.e, this.getGlId(), 256, 256);
    }
    
    @Override
    public void load(final ResourceManager resourceManager) {
    }
    
    @Override
    public void close() {
        this.clearGlId();
    }
    
    @Nullable
    public GlyphRenderer getGlyphRenderer(final RenderableGlyph glyph) {
        if (glyph.hasColor() != this.hasColor) {
            return null;
        }
        final Slot slot2 = this.rootSlot.findSlotFor(glyph);
        if (slot2 != null) {
            this.bindTexture();
            glyph.upload(slot2.x, slot2.y);
            final float float3 = 256.0f;
            final float float4 = 256.0f;
            final float float5 = 0.01f;
            return new GlyphRenderer(this.id, (slot2.x + 0.01f) / 256.0f, (slot2.x - 0.01f + glyph.getWidth()) / 256.0f, (slot2.y + 0.01f) / 256.0f, (slot2.y - 0.01f + glyph.getHeight()) / 256.0f, glyph.getXMin(), glyph.getXMax(), glyph.getYMin(), glyph.getYMax());
        }
        return null;
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    @Environment(EnvType.CLIENT)
    static class Slot
    {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private Slot subSlot1;
        private Slot subSlot2;
        private boolean isOccupied;
        
        private Slot(final int integer1, final int integer2, final int integer3, final int integer4) {
            this.x = integer1;
            this.y = integer2;
            this.width = integer3;
            this.height = integer4;
        }
        
        @Nullable
        Slot findSlotFor(final RenderableGlyph glyph) {
            if (this.subSlot1 != null && this.subSlot2 != null) {
                Slot slot2 = this.subSlot1.findSlotFor(glyph);
                if (slot2 == null) {
                    slot2 = this.subSlot2.findSlotFor(glyph);
                }
                return slot2;
            }
            if (this.isOccupied) {
                return null;
            }
            final int integer2 = glyph.getWidth();
            final int integer3 = glyph.getHeight();
            if (integer2 > this.width || integer3 > this.height) {
                return null;
            }
            if (integer2 == this.width && integer3 == this.height) {
                this.isOccupied = true;
                return this;
            }
            final int integer4 = this.width - integer2;
            final int integer5 = this.height - integer3;
            if (integer4 > integer5) {
                this.subSlot1 = new Slot(this.x, this.y, integer2, this.height);
                this.subSlot2 = new Slot(this.x + integer2 + 1, this.y, this.width - integer2 - 1, this.height);
            }
            else {
                this.subSlot1 = new Slot(this.x, this.y, this.width, integer3);
                this.subSlot2 = new Slot(this.x, this.y + integer3 + 1, this.width, this.height - integer3 - 1);
            }
            return this.subSlot1.findSlotFor(glyph);
        }
    }
}
