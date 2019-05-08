package net.minecraft.client.font;

import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.LogManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import java.nio.IntBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.stb.STBTruetype;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import org.lwjgl.stb.STBTTFontinfo;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TrueTypeFont implements Font
{
    private static final Logger LOGGER;
    private final STBTTFontinfo info;
    private final float oversample;
    private final CharSet excludedCharacters;
    private final float shiftX;
    private final float shiftY;
    private final float scaleFactor;
    private final float ascent;
    
    public TrueTypeFont(final STBTTFontinfo info, final float float2, final float oversample, final float shiftX, final float shiftY, final String excludedCharacters) {
        this.excludedCharacters = (CharSet)new CharArraySet();
        this.info = info;
        this.oversample = oversample;
        excludedCharacters.chars().forEach(integer -> this.excludedCharacters.add((char)(integer & 0xFFFF)));
        this.shiftX = shiftX * oversample;
        this.shiftY = shiftY * oversample;
        this.scaleFactor = STBTruetype.stbtt_ScaleForPixelHeight(info, float2 * oversample);
        try (final MemoryStack memoryStack7 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer9 = memoryStack7.mallocInt(1);
            final IntBuffer intBuffer10 = memoryStack7.mallocInt(1);
            final IntBuffer intBuffer11 = memoryStack7.mallocInt(1);
            STBTruetype.stbtt_GetFontVMetrics(info, intBuffer9, intBuffer10, intBuffer11);
            this.ascent = intBuffer9.get(0) * this.scaleFactor;
        }
    }
    
    @Nullable
    @Override
    public TtfGlyph getGlyph(final char character) {
        if (this.excludedCharacters.contains(character)) {
            return null;
        }
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final IntBuffer intBuffer4 = memoryStack2.mallocInt(1);
            final IntBuffer intBuffer5 = memoryStack2.mallocInt(1);
            final IntBuffer intBuffer6 = memoryStack2.mallocInt(1);
            final IntBuffer intBuffer7 = memoryStack2.mallocInt(1);
            final int integer8 = STBTruetype.stbtt_FindGlyphIndex(this.info, (int)character);
            if (integer8 == 0) {
                return null;
            }
            STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.info, integer8, this.scaleFactor, this.scaleFactor, this.shiftX, this.shiftY, intBuffer4, intBuffer5, intBuffer6, intBuffer7);
            final int integer9 = intBuffer6.get(0) - intBuffer4.get(0);
            final int integer10 = intBuffer7.get(0) - intBuffer5.get(0);
            if (integer9 == 0 || integer10 == 0) {
                return null;
            }
            final IntBuffer intBuffer8 = memoryStack2.mallocInt(1);
            final IntBuffer intBuffer9 = memoryStack2.mallocInt(1);
            STBTruetype.stbtt_GetGlyphHMetrics(this.info, integer8, intBuffer8, intBuffer9);
            return new TtfGlyph(intBuffer4.get(0), intBuffer6.get(0), -intBuffer5.get(0), -intBuffer7.get(0), intBuffer8.get(0) * this.scaleFactor, intBuffer9.get(0) * this.scaleFactor, integer8);
        }
    }
    
    public static STBTTFontinfo getSTBTTFontInfo(final ByteBuffer font) throws IOException {
        final STBTTFontinfo sTBTTFontinfo2 = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(sTBTTFontinfo2, font)) {
            throw new IOException("Invalid ttf");
        }
        return sTBTTFontinfo2;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    class TtfGlyph implements RenderableGlyph
    {
        private final int width;
        private final int height;
        private final float bearingX;
        private final float ascent;
        private final float advance;
        private final int glyphIndex;
        
        private TtfGlyph(final int xMax, final int yMax, final int yMin, final int advance, final float float6, final float glyphIndex, final int integer8) {
            this.width = yMax - xMax;
            this.height = yMin - advance;
            this.advance = float6 / TrueTypeFont.this.oversample;
            this.bearingX = (glyphIndex + xMax + TrueTypeFont.this.shiftX) / TrueTypeFont.this.oversample;
            this.ascent = (TrueTypeFont.this.ascent - yMin + TrueTypeFont.this.shiftY) / TrueTypeFont.this.oversample;
            this.glyphIndex = integer8;
        }
        
        @Override
        public int getWidth() {
            return this.width;
        }
        
        @Override
        public int getHeight() {
            return this.height;
        }
        
        @Override
        public float getOversample() {
            return TrueTypeFont.this.oversample;
        }
        
        @Override
        public float getAdvance() {
            return this.advance;
        }
        
        @Override
        public float getBearingX() {
            return this.bearingX;
        }
        
        @Override
        public float getAscent() {
            return this.ascent;
        }
        
        @Override
        public void upload(final int x, final int y) {
            try (final NativeImage nativeImage3 = new NativeImage(NativeImage.Format.d, this.width, this.height, false)) {
                nativeImage3.makeGlyphBitmapSubpixel(TrueTypeFont.this.info, this.glyphIndex, this.width, this.height, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.scaleFactor, TrueTypeFont.this.shiftX, TrueTypeFont.this.shiftY, 0, 0);
                nativeImage3.upload(0, x, y, 0, 0, this.width, this.height, false);
            }
        }
        
        @Override
        public boolean hasColor() {
            return false;
        }
    }
}
