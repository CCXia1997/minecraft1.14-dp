package net.minecraft.client.font;

import net.minecraft.resource.Resource;
import java.io.IOException;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraft.resource.ResourceManager;
import com.google.gson.JsonArray;
import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonHelper;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureFont implements Font
{
    private static final Logger LOGGER;
    private final NativeImage image;
    private final Char2ObjectMap<TextureFontGlyph> characterToGlyphMap;
    
    public TextureFont(final NativeImage image, final Char2ObjectMap<TextureFontGlyph> char2ObjectMap) {
        this.image = image;
        this.characterToGlyphMap = char2ObjectMap;
    }
    
    @Override
    public void close() {
        this.image.close();
    }
    
    @Nullable
    @Override
    public RenderableGlyph getGlyph(final char character) {
        return (RenderableGlyph)this.characterToGlyphMap.get(character);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public static class Loader implements FontLoader
    {
        private final Identifier filename;
        private final List<String> chars;
        private final int height;
        private final int ascent;
        
        public Loader(final Identifier id, final int height, final int ascent, final List<String> chars) {
            this.filename = new Identifier(id.getNamespace(), "textures/" + id.getPath());
            this.chars = chars;
            this.height = height;
            this.ascent = ascent;
        }
        
        public static Loader fromJson(final JsonObject json) {
            final int integer2 = JsonHelper.getInt(json, "height", 8);
            final int integer3 = JsonHelper.getInt(json, "ascent");
            if (integer3 > integer2) {
                throw new JsonParseException("Ascent " + integer3 + " higher than height " + integer2);
            }
            final List<String> list4 = Lists.newArrayList();
            final JsonArray jsonArray5 = JsonHelper.getArray(json, "chars");
            for (int integer4 = 0; integer4 < jsonArray5.size(); ++integer4) {
                final String string7 = JsonHelper.asString(jsonArray5.get(integer4), "chars[" + integer4 + "]");
                if (integer4 > 0) {
                    final int integer5 = string7.length();
                    final int integer6 = list4.get(0).length();
                    if (integer5 != integer6) {
                        throw new JsonParseException("Elements of chars have to be the same length (found: " + integer5 + ", expected: " + integer6 + "), pad with space or \\u0000");
                    }
                }
                list4.add(string7);
            }
            if (list4.isEmpty() || list4.get(0).isEmpty()) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Loader(new Identifier(JsonHelper.getString(json, "file")), integer2, integer3, list4);
        }
        
        @Nullable
        @Override
        public Font load(final ResourceManager resourceManager) {
            try (final Resource resource2 = resourceManager.getResource(this.filename)) {
                final NativeImage nativeImage4 = NativeImage.fromInputStream(NativeImage.Format.a, resource2.getInputStream());
                final int integer5 = nativeImage4.getWidth();
                final int integer6 = nativeImage4.getHeight();
                final int integer7 = integer5 / this.chars.get(0).length();
                final int integer8 = integer6 / this.chars.size();
                final float float9 = this.height / (float)integer8;
                final Char2ObjectMap<TextureFontGlyph> char2ObjectMap10 = (Char2ObjectMap<TextureFontGlyph>)new Char2ObjectOpenHashMap();
                for (int integer9 = 0; integer9 < this.chars.size(); ++integer9) {
                    final String string12 = this.chars.get(integer9);
                    for (int integer10 = 0; integer10 < string12.length(); ++integer10) {
                        final char character14 = string12.charAt(integer10);
                        if (character14 != '\0') {
                            if (character14 != ' ') {
                                final int integer11 = this.findCharacterStartX(nativeImage4, integer7, integer8, integer10, integer9);
                                final TextureFontGlyph textureFontGlyph16 = (TextureFontGlyph)char2ObjectMap10.put(character14, new TextureFontGlyph(float9, nativeImage4, integer10 * integer7, integer9 * integer8, integer7, integer8, (int)(0.5 + integer11 * float9) + 1, this.ascent));
                                if (textureFontGlyph16 != null) {
                                    TextureFont.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(character14), this.filename);
                                }
                            }
                        }
                    }
                }
                return new TextureFont(nativeImage4, char2ObjectMap10);
            }
            catch (IOException iOException2) {
                throw new RuntimeException(iOException2.getMessage());
            }
        }
        
        private int findCharacterStartX(final NativeImage image, final int characterWidth, final int characterHeight, final int charPosX, final int charPosY) {
            int integer6;
            for (integer6 = characterWidth - 1; integer6 >= 0; --integer6) {
                final int integer7 = charPosX * characterWidth + integer6;
                for (int integer8 = 0; integer8 < characterHeight; ++integer8) {
                    final int integer9 = charPosY * characterHeight + integer8;
                    if (image.getAlphaOrLuminance(integer7, integer9) != 0) {
                        return integer6 + 1;
                    }
                }
            }
            return integer6 + 1;
        }
    }
    
    @Environment(EnvType.CLIENT)
    static final class TextureFontGlyph implements RenderableGlyph
    {
        private final float scaleFactor;
        private final NativeImage image;
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final int advance;
        private final int ascent;
        
        private TextureFontGlyph(final float scaleFactor, final NativeImage image, final int x, final int y, final int width, final int height, final int advance, final int ascent) {
            this.scaleFactor = scaleFactor;
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.advance = advance;
            this.ascent = ascent;
        }
        
        @Override
        public float getOversample() {
            return 1.0f / this.scaleFactor;
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
        public float getAdvance() {
            return (float)this.advance;
        }
        
        @Override
        public float getAscent() {
            return super.getAscent() + 7.0f - this.ascent;
        }
        
        @Override
        public void upload(final int x, final int y) {
            this.image.upload(0, x, y, this.x, this.y, this.width, this.height, false);
        }
        
        @Override
        public boolean hasColor() {
            return this.image.getFormat().getBytesPerPixel() > 1;
        }
    }
}
